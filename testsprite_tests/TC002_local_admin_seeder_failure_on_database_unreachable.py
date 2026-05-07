import requests
from requests.auth import HTTPBasicAuth

def test_local_admin_seeder_failure_on_database_unreachable():
    base_url = "http://localhost:8080"
    timeout = 30
    auth = HTTPBasicAuth("john1237", "123456")

    # The application should be started with spring.profiles.active=local and database unreachable.
    # This test verifies that the seeder logs connection failure and startup does not complete normally.
    # The only indirect way to confirm failure is that /auth/session is not accessible with admin credentials,
    # or the app returns errors on admin authenticated endpoints.
    # Since we cannot read logs via API we check for service unavailability or auth failure.

    # Attempt to authenticate and get session info with admin credentials
    session_url = f"{base_url}/auth/session"

    try:
        response = requests.get(session_url, auth=auth, timeout=timeout)
    except requests.exceptions.RequestException as e:
        # Expecting failure to connect or timeout due to DB unreachable causing app startup failure
        assert True, f"Service connection error as expected due to DB unreachable: {str(e)}"
        return

    # If connected, check status code and response for error indication
    # Normal startup would authenticate admin user and return 200 with session info
    # Failure or incomplete startup should result in 5xx errors or auth failure
    if response.status_code == 200:
        # The session endpoint returned success, meaning app started normally - test fails
        assert False, "Application started normally, expected failure due to database unreachable."
    else:
        # Accept 401 Unauthorized or 503 Service Unavailable or 500 Internal Server Error
        assert response.status_code in (401, 403, 500, 503), (
            f"Expected failure status code (401,403,500,503), got {response.status_code}"
        )

    # Additionally, test an unauthenticated open endpoint works (like /v3/api-docs)
    docs_url = f"{base_url}/v3/api-docs"
    try:
        docs_response = requests.get(docs_url, timeout=timeout)
        # API docs endpoint may be accessible even if DB is down, or not.
        # We accept either it fails with 5xx or returns 200 with some content.
        if docs_response.status_code == 200:
            # If 200, verify it contains expected OpenAPI fields (like "openapi")
            json_data = docs_response.json()
            assert "openapi" in json_data, "/v3/api-docs response missing 'openapi' key"
        else:
            # Docs endpoint returned an error, acceptable if DB unreachable
            assert docs_response.status_code >= 500, "Unexpected non-5xx status from /v3/api-docs"
    except requests.exceptions.RequestException:
        # Network or connect failure is acceptable as DB unavailability impacts startup
        pass

test_local_admin_seeder_failure_on_database_unreachable()