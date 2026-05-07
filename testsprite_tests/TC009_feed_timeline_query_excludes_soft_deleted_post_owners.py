import requests
from requests.auth import HTTPBasicAuth

BASE_URL = "http://localhost:8080"
USERNAME = "john1237"
PASSWORD = "123456"
TIMEOUT = 30

def test_feed_timeline_query_excludes_soft_deleted_post_owners():
    """
    Verify that GET /posts/** excludes posts or reposts belonging to soft-deleted users by evaluating the deleted flag,
    ensuring feed remains free of deleted-user content.
    """
    auth = HTTPBasicAuth(USERNAME, PASSWORD)
    headers = {
        "Accept": "application/json"
    }

    try:
        response = requests.get(f"{BASE_URL}/posts", auth=auth, headers=headers, timeout=TIMEOUT)
    except requests.RequestException as e:
        assert False, f"Request to /posts failed: {e}"

    # Assert HTTP status 200 OK
    assert response.status_code == 200, f"Expected status code 200, got {response.status_code}"

    try:
        data = response.json()
    except ValueError:
        assert False, "Response content is not valid JSON"

    # Assert the JSON response is a dict and contains expected keys (e.g. a list of posts)
    assert isinstance(data, dict), "Response JSON root is not an object/dict"
    # We expect one or more of: 'posts', 'content', or a list root depending on API design
    posts = None
    if "posts" in data and isinstance(data["posts"], list):
        posts = data["posts"]
    elif "content" in data and isinstance(data["content"], list):
        posts = data["content"]
    elif isinstance(data, list):
        posts = data
    else:
        assert False, "Response JSON does not contain a list of posts in expected keys"

    # Validate each post: it must NOT be owned by a soft-deleted user.
    # We assume each post has an 'author' object with a 'deleted' boolean flag.
    for post in posts:
        assert isinstance(post, dict), "Post item is not a dict/object"
        author = post.get("author") or post.get("user")  # tolerate possible naming
        assert author is not None, "Post missing author/user field"
        deleted_flag = author.get("deleted")
        # deleted can be False or None treated as false (coalesce deleted false)
        assert deleted_flag in (False, None), (
            f"Post from soft-deleted user found: user id={author.get('id')}, deleted={deleted_flag}"
        )

test_feed_timeline_query_excludes_soft_deleted_post_owners()