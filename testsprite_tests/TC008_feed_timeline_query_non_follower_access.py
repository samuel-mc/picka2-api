import requests

BASE_URL = "http://localhost:8080"
USERNAME = "admin"
PASSWORD = "123456"
TIMEOUT = 30

def test_feed_timeline_query_non_follower_access():
    """
    As an authenticated user who is not a follower,
    verify that FOLLOWERS_ONLY and PRIVATE posts from non-followed authors 
    are omitted and only allowed PUBLIC content is returned in GET /posts/**.
    """
    # 1. Authenticate to get JWT token
    login_url = f"{BASE_URL}/auth/login"
    login_payload = {
        "username": USERNAME,
        "password": PASSWORD
    }
    headers = {"Accept": "application/json"}

    try:
        login_resp = requests.post(login_url, json=login_payload, headers=headers, timeout=TIMEOUT)
        login_resp.raise_for_status()
    except requests.RequestException as e:
        assert False, f"Login request failed: {e}"

    try:
        login_data = login_resp.json()
    except ValueError:
        assert False, "Login response is not valid JSON"

    token = login_data.get("token") or login_data.get("accessToken") or login_data.get("jwt")
    assert token and isinstance(token, str), "Login response missing JWT token"

    # 2. Use token in Authorization header for feed request
    url = f"{BASE_URL}/posts"
    auth_headers = {
        "Accept": "application/json",
        "Authorization": f"Bearer {token}"
    }

    try:
        response = requests.get(url, headers=auth_headers, timeout=TIMEOUT)
        response.raise_for_status()
    except requests.RequestException as e:
        assert False, f"Request to GET /posts failed: {e}"

    assert response.status_code == 200, f"Expected HTTP status 200 but got {response.status_code}"

    try:
        data = response.json()
    except ValueError:
        assert False, "Response is not valid JSON"

    posts = None
    if isinstance(data, dict):
        posts = data.get("content", None)
        if posts is None:
            posts = data.get("posts", None)
        if posts is None:
            posts = data if isinstance(data, list) else []
    elif isinstance(data, list):
        posts = data
    else:
        assert False, "Unexpected JSON shape: expected list or dict"

    assert isinstance(posts, list), f"Expected posts to be a list but got {type(posts)}"

    allowed_visibilities = {"PUBLIC"}

    for post in posts:
        assert isinstance(post, dict), f"Each post should be dict, got {type(post)}"
        visibility = post.get("visibility")
        assert isinstance(visibility, str), "Post missing 'visibility' field or not string"
        assert visibility in allowed_visibilities, (
            f"Non-follower feed must exclude FOLLOWERS_ONLY/PRIVATE posts, found post with visibility '{visibility}'"
        )
        author = post.get("author")
        if author:
            deleted = author.get("deleted", False)
            assert deleted is False, "Post author must not be soft-deleted"

    assert posts is not None, "Posts list should not be None"


test_feed_timeline_query_non_follower_access()
