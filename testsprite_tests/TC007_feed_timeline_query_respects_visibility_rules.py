import requests

BASE_URL = "http://localhost:8080"
LOGIN_PATH = "/auth/login"
POSTS_FEED_PATH = "/posts/feed"
USERNAME = "admin"
PASSWORD = "123456"
TIMEOUT = 30

def test_feed_timeline_query_respects_visibility_rules():
    session = requests.Session()
    # Authenticate and obtain JWT cookie
    login_payload = {"username": USERNAME, "password": PASSWORD}
    login_resp = session.post(f"{BASE_URL}{LOGIN_PATH}", json=login_payload, timeout=TIMEOUT)
    assert login_resp.status_code == 200, f"Login failed: {login_resp.status_code} {login_resp.text}"
    assert "jwt" in session.cookies or any("jwt" in c.name.lower() for c in session.cookies), "JWT cookie not set after login"

    # Fetch authenticated user's profile to get own userId
    profile_resp = session.get(f"{BASE_URL}/me/profile", timeout=TIMEOUT)
    assert profile_resp.status_code == 200, f"Get profile failed: {profile_resp.status_code} {profile_resp.text}"
    profile_data = profile_resp.json()
    own_user_id = profile_data.get("id")
    assert own_user_id is not None, "Own user ID not found in profile"

    # Retrieve the feed/timeline posts
    feed_resp = session.get(f"{BASE_URL}{POSTS_FEED_PATH}", timeout=TIMEOUT)
    assert feed_resp.status_code == 200, f"Feed retrieval failed: {feed_resp.status_code} {feed_resp.text}"
    posts = feed_resp.json()
    # posts expected to be a list
    assert isinstance(posts, list), "Feed response is not a list"

    for post in posts:
        post_visibility = post.get("visibility")
        post_author = post.get("author", {})
        author_id = post_author.get("id")
        assert post_visibility in {"PUBLIC", "FOLLOWERS_ONLY", "PRIVATE"}, f"Unexpected visibility value: {post_visibility}"

        if post_visibility == "PUBLIC":
            continue

        elif post_visibility == "FOLLOWERS_ONLY":
            assert author_id is not None, "Post author ID missing for FOLLOWERS_ONLY post"

        elif post_visibility == "PRIVATE":
            assert author_id == own_user_id, f"Private post visible to non-author user. Post ID: {post.get('id')}"

    for post in posts:
        post_author = post.get("author", {})
        if "deleted" in post_author:
            assert post_author["deleted"] is False, f"Post by deleted author found: post {post.get('id')}"

test_feed_timeline_query_respects_visibility_rules()
