import requests
from requests.auth import HTTPBasicAuth

def test_local_postgresql_environment_connection():
    base_url = "http://localhost:8080"
    login_url = f"{base_url}/auth/login"
    session_url = f"{base_url}/auth/session"
    timeout = 30

    # Use seeded admin user credentials
    username = "admin"
    password = "123456"

    try:
        # Step 1: Login to get auth cookie (JWT)
        login_payload = {
            "username": username,
            "password": password
        }
        login_headers = {
            "Content-Type": "application/json"
        }
        login_response = requests.post(login_url, json=login_payload, headers=login_headers, timeout=timeout)
        assert login_response.status_code == 200, f"Login failed with status code {login_response.status_code}"
        assert 'jwt' in login_response.cookies or 'JSESSIONID' in login_response.cookies or login_response.headers.get("set-cookie"), "Login response did not set an auth cookie"

        # Extract auth cookie from response
        cookies = login_response.cookies

        # Step 2: Access authenticated session endpoint to verify connection and authentication
        session_response = requests.get(session_url, cookies=cookies, timeout=timeout)
        assert session_response.status_code == 200, f"Session request failed with status code {session_response.status_code}"

        # Validate JSON shape and seeded admin properties
        session_json = session_response.json()
        # Check keys expected for session info
        assert isinstance(session_json, dict), "Session response is not a JSON object"
        assert "username" in session_json, "Session JSON missing 'username' field"
        assert "email" in session_json, "Session JSON missing 'email' field"
        assert session_json["username"] == "admin" or session_json["username"] == username, "Session username is not admin or the test user"
        # Check for admin seed user with expected email if username is admin
        if session_json["username"] == "admin":
            assert session_json["email"] == "admin@pickados.local", "Admin seeded user email mismatch"

        # Additional properties like active and deleted might be checked if exposed
        # But limited by available API info
    except requests.RequestException as e:
        assert False, f"Request failed: {str(e)}"

test_local_postgresql_environment_connection()