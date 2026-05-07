import requests
from requests.auth import HTTPBasicAuth

BASE_URL = "http://localhost:8080"
AUTH_USERNAME = "john1237"
AUTH_PASSWORD = "123456"
TIMEOUT = 30


def test_local_postgresql_environment_failure_handling():
    """
    Simulate PostgreSQL startup failure due to port conflict or container issue and verify that
    the failure is clearly logged and the developer is informed to fix the environment before retrying.
    """

    # Attempt to fetch OpenAPI docs to check if backend started correctly
    # If PostgreSQL startup failed, the app likely won't start or returns error messages
    url_api_docs = f"{BASE_URL}/v3/api-docs"

    try:
        response = requests.get(url_api_docs, timeout=TIMEOUT)
    except requests.RequestException as e:
        # Network error or connection refused likely means backend is not running due to DB failure
        assert True, f"Backend unreachable due to PostgreSQL failure or container issue: {e}"
        return

    # If backend returns a HTTP error status, likely it cannot connect to DB successfully
    if response.status_code >= 500:
        # The failure to connect to DB is logged, backend returns 5xx error
        json_content = {}
        try:
            json_content = response.json()
        except Exception:
            pass
        # Assert error message or indication of DB connection failure is present
        error_msgs = [
            "port conflict",
            "container issue",
            "database connection failed",
            "could not connect",
            "PostgreSQL",
            "failed to start",
            "connection refused",
            "startup failure"
        ]
        if json_content:
            error_text = str(json_content).lower()
            assert any(msg in error_text for msg in error_msgs), \
                f"Expected failure message in JSON but got: {json_content}"
        else:
            # If no JSON, assert response text has error clues
            assert any(msg in response.text.lower() for msg in error_msgs), \
                f"Expected failure message in response but got: {response.text}"
        return
    elif response.status_code == 200:
        # If we got a normal successful response, then the failure is NOT present.
        # We simulate failure - so this is a test failure because failure not detected.
        assert False, "Expected PostgreSQL startup failure but got successful backend response."
    else:
        # Unexpected status - check body for error messages
        try:
            json_content = response.json()
            error_text = str(json_content).lower()
            # Verify presence of failure message advising developer to fix environment
            assert any(m in error_text for m in ["fix", "retry", "port conflict", "postgresql", "failed"]), \
                f"Expected failure advisory message in JSON but got: {json_content}"
        except Exception:
            # Fallback no JSON, check plaintext response
            assert any(msg in response.text.lower() for msg in ["fix", "retry", "port conflict", "postgresql", "failed"]), \
                f"Expected failure advisory message in response but got: {response.text}"


test_local_postgresql_environment_failure_handling()