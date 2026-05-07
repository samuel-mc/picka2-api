import requests
from requests.auth import HTTPBasicAuth

BASE_URL = "http://localhost:8080"
HEADERS = {
    "Accept": "application/json"
}
TIMEOUT = 30

def test_public_post_detail_access_control():
    """
    Verify that GET /posts/public/{postId} returns 404 or access-denied response
    when the post is owned by a deleted author or is a private post,
    preventing exposure of sensitive content.
    """
    post_id = None
    user_id = None
    user_post_id = None
    # Admin credentials
    admin_auth = HTTPBasicAuth("admin", "123456")

    try:
        # Step 1: Create a new tipster user for test (needed for creating posts)
        user_payload = {
            "username": "testdeleteduser123",
            "email": "testdeleteduser123@pickados.local",
            "password": "123456",
            "roles": ["TIPSTER"],
            "birthDate": "1980-01-01"
        }
        create_user_resp = requests.post(
            f"{BASE_URL}/auth/register-tipster",
            json=user_payload,
            headers={"Accept": "application/json", "Content-Type": "application/json"},
            auth=admin_auth,
            timeout=TIMEOUT
        )
        assert create_user_resp.status_code == 201, \
            f"User creation failed: {create_user_resp.status_code} {create_user_resp.text}"
        created_user = create_user_resp.json()
        user_id = created_user.get("id")
        assert user_id is not None, "Created user ID not returned"

        # Authenticate as the new tipster user
        tipster_auth = HTTPBasicAuth(user_payload["username"], user_payload["password"])

        # Create a new post with private visibility (since we test private post access)
        post_payload = {
            "content": "This is a private post for access control test",
            "visibility": "PRIVATE"  # Assuming visibility field accepts PUBLIC, FOLLOWERS_ONLY, PRIVATE
        }

        # Create a post (POST /posts requires tipster authentication)
        create_post_resp = requests.post(
            f"{BASE_URL}/posts",
            json=post_payload,
            auth=tipster_auth,
            headers={"Accept": "application/json", "Content-Type": "application/json"},
            timeout=TIMEOUT
        )
        assert create_post_resp.status_code == 201, \
            f"Post creation failed: {create_post_resp.status_code} {create_post_resp.text}"
        post_data = create_post_resp.json()
        post_id = post_data.get("id")
        assert post_id is not None, "Created post ID not returned"

        # Attempt to GET the public post detail for this private post (unauthenticated)
        get_resp_private = requests.get(
            f"{BASE_URL}/posts/public/{post_id}",
            headers=HEADERS,
            timeout=TIMEOUT
        )
        # Should return 404 or access-denied status (e.g. 403 or 404)
        assert get_resp_private.status_code in (403, 404), \
            f"Expected 403 or 404 for private post, got {get_resp_private.status_code}"
        # If JSON response, verify it contains an error indication
        if get_resp_private.headers.get("Content-Type", "").startswith("application/json"):
            error_json = get_resp_private.json()
            assert ("error" in error_json or "message" in error_json or "status" in error_json), \
                f"Error response JSON shape invalid: {error_json}"

        # Step 2: Create a post owned by this new user with PUBLIC visibility
        user_post_payload = {
            "content": "Post by user who will be deleted",
            "visibility": "PUBLIC"
        }
        create_user_post_resp = requests.post(
            f"{BASE_URL}/posts",
            json=user_post_payload,
            auth=tipster_auth,
            headers={"Accept": "application/json", "Content-Type": "application/json"},
            timeout=TIMEOUT
        )
        assert create_user_post_resp.status_code == 201, \
            f"User post creation failed: {create_user_post_resp.status_code} {create_user_post_resp.text}"
        user_post_data = create_user_post_resp.json()
        user_post_id = user_post_data.get("id")
        assert user_post_id is not None, "User post ID not returned"

        # Soft-delete the user (admin required)
        delete_user_resp = requests.delete(
            f"{BASE_URL}/users/{user_id}",
            auth=admin_auth,
            headers={"Accept": "application/json"},
            timeout=TIMEOUT
        )
        assert delete_user_resp.status_code in (200, 204), \
            f"Failed to soft-delete user: {delete_user_resp.status_code} {delete_user_resp.text}"

        # Attempt to GET the public post detail for post owned by deleted user
        get_resp_deleted_owner = requests.get(
            f"{BASE_URL}/posts/public/{user_post_id}",
            headers=HEADERS,
            timeout=TIMEOUT
        )
        # Should return 404 or access-denied (403) to prevent exposing content
        assert get_resp_deleted_owner.status_code in (403, 404), \
            f"Expected 403 or 404 for post owned by deleted user, got {get_resp_deleted_owner.status_code}"
        if get_resp_deleted_owner.headers.get("Content-Type", "").startswith("application/json"):
            error_json = get_resp_deleted_owner.json()
            assert ("error" in error_json or "message" in error_json or "status" in error_json), \
                f"Error response JSON shape invalid for deleted user post: {error_json}"

    finally:
        # Cleanup: delete created posts and users if possible
        if post_id is not None:
            requests.delete(
                f"{BASE_URL}/posts/{post_id}",
                auth=tipster_auth,
                headers={"Accept": "application/json"},
                timeout=TIMEOUT
            )
        if user_post_id is not None:
            requests.delete(
                f"{BASE_URL}/posts/{user_post_id}",
                auth=tipster_auth,
                headers={"Accept": "application/json"},
                timeout=TIMEOUT
            )
        if user_id is not None:
            # Try hard delete again
            requests.delete(
                f"{BASE_URL}/users/{user_id}",
                auth=admin_auth,
                headers={"Accept": "application/json"},
                timeout=TIMEOUT
            )

test_public_post_detail_access_control()
