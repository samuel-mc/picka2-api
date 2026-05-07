import requests

BASE_URL = "http://localhost:8080"
TIMEOUT = 30


def get_auth_session(username, password):
    login_url = f"{BASE_URL}/auth/login"
    payload = {"username": username, "password": password}
    headers = {"Content-Type": "application/json", "Accept": "application/json"}
    response = requests.post(login_url, json=payload, headers=headers, timeout=TIMEOUT)
    assert response.status_code == 200, f"Login failed with status {response.status_code}"
    # Expect the server to set a session cookie for authentication
    cookies = response.cookies
    return cookies


def test_feed_timeline_excludes_soft_deleted_users_and_respects_visibility():
    username = "john1237"
    password = "123456"
    cookies = get_auth_session(username, password)

    posts_url = f"{BASE_URL}/posts/feed"
    headers = {"Accept": "application/json"}

    # Request posts feed with authentication using session cookies
    response = requests.get(posts_url, cookies=cookies, headers=headers, timeout=TIMEOUT)
    assert response.status_code == 200, f"Expected 200 OK, got {response.status_code}"

    try:
        posts = response.json()
    except Exception as e:
        assert False, f"Response is not valid JSON: {e}"

    assert isinstance(posts, list), f"Expected posts as list, got {type(posts)}"

    # Helper sets for visibility rules validation
    # A post is expected to have at least: id, author {id, deleted}, visibility, and optionally followers list
    for post in posts:
        assert "author" in post, "Post missing author"
        author = post["author"]
        assert isinstance(author, dict), "Author info is not a dict"
        assert "deleted" in author, "Author missing 'deleted' flag"
        assert author["deleted"] is False, "Post author is soft-deleted, should be excluded"

        visibility = post.get("visibility")
        assert visibility in {"PUBLIC", "FOLLOWERS_ONLY", "PRIVATE"}, f"Unexpected visibility value: {visibility}"

        if visibility == "PRIVATE":
            user_profile_resp = requests.get(f"{BASE_URL}/me/profile", cookies=cookies, headers=headers, timeout=TIMEOUT)
            assert user_profile_resp.status_code == 200, f"Failed to fetch user profile, status {user_profile_resp.status_code}"
            try:
                user_profile = user_profile_resp.json()
            except Exception as e:
                assert False, f"User profile response not valid JSON: {e}"
            assert author.get("id") == user_profile.get("id"), "PRIVATE post author ID does not match authenticated user ID"

        elif visibility == "FOLLOWERS_ONLY":
            user_profile_resp = requests.get(f"{BASE_URL}/me/profile", cookies=cookies, headers=headers, timeout=TIMEOUT)
            assert user_profile_resp.status_code == 200, f"Failed to fetch user profile, status {user_profile_resp.status_code}"
            try:
                user_profile = user_profile_resp.json()
            except Exception as e:
                assert False, f"User profile response not valid JSON: {e}"

            user_id = user_profile.get("id")
            author_id = author.get("id")

            followers = post.get("followers")
            assert followers is not None, "Missing followers list on FOLLOWERS_ONLY post"

            is_author = user_id == author_id
            is_follower = False
            if followers:
                if all(isinstance(f, dict) for f in followers):
                    follower_ids = {f.get("id") for f in followers}
                else:
                    follower_ids = set(followers)
                is_follower = user_id in follower_ids

            assert is_author or is_follower, "Authenticated user cannot see FOLLOWERS_ONLY post without being author or follower"

        else:
            pass


test_feed_timeline_excludes_soft_deleted_users_and_respects_visibility()
