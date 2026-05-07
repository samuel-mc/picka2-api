import requests

BASE_URL = "http://localhost:8080"
LOGIN_PATH = "/auth/login"
POSTS_FEED_PATH = "/posts"

USERNAME = "john1237"
PASSWORD = "123456"
TIMEOUT = 30

def test_feed_timeline_query_excludes_soft_deleted_users():
    session = requests.Session()
    try:
        # Authenticate and store JWT cookie in session
        auth_payload = {"username": USERNAME, "password": PASSWORD}
        login_resp = session.post(f"{BASE_URL}{LOGIN_PATH}", json=auth_payload, timeout=TIMEOUT)
        assert login_resp.status_code == 200, f"Login failed with status {login_resp.status_code}"
        assert "jwt" in login_resp.cookies or any("jwt" in c.name.lower() for c in login_resp.cookies), "JWT cookie not found after login"

        # Call GET /posts/feed or /posts (feed/timeline endpoint)
        # We test with /posts as per PRD guidance and test instructions
        feed_resp = session.get(f"{BASE_URL}{POSTS_FEED_PATH}", timeout=TIMEOUT)
        assert feed_resp.status_code == 200, f"Feed request failed with status {feed_resp.status_code}"

        posts = feed_resp.json()
        # posts expected to be a list of post objects
        assert isinstance(posts, list), "Feed response is not a list"

        # Check no post is authored by a user with deleted=true
        # We assume each post has 'author' object with 'deleted' boolean field
        for post in posts:
            assert 'author' in post, "Post missing author field"
            author = post['author']
            assert isinstance(author, dict), "Author field is not an object"
            # deleted might be missing or explicitly false, but must not be true
            deleted_flag = author.get('deleted', False)
            assert deleted_flag is False, f"Found post by soft-deleted user. Post ID: {post.get('id')} Author: {author.get('id')}"
    finally:
        session.close()

test_feed_timeline_query_excludes_soft_deleted_users()