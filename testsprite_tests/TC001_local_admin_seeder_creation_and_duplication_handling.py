import requests

BASE_URL = "http://localhost:8080"
AUTH_USERNAME = "john1237"
AUTH_PASSWORD = "123456"
TIMEOUT = 30


def test_local_admin_seeder_creation_and_duplication_handling():
    # Step 1: Verify GET /auth/session without auth returns 401 or 403 Unauthorized/Forbidden
    session_url = f"{BASE_URL}/auth/session"
    try:
        r = requests.get(session_url, timeout=TIMEOUT)
        assert r.status_code in (401, 403), f"Expected 401 or 403 for unauthenticated access, got {r.status_code}"
    except requests.RequestException as e:
        assert False, f"Request to /auth/session failed unexpectedly: {e}"

    # Step 2: Authenticate via POST /auth/login with JSON payload to get auth cookie
    login_url = f"{BASE_URL}/auth/login"
    login_payload = {"username": AUTH_USERNAME, "password": AUTH_PASSWORD}
    try:
        r = requests.post(login_url, json=login_payload, timeout=TIMEOUT)
        assert r.status_code == 200, f"Expected 200 for login, got {r.status_code}"
        # Extract auth cookie
        cookies = r.cookies
    except requests.RequestException as e:
        assert False, f"Login request failed: {e}"
    except ValueError:
        assert False, f"Response from /auth/login is not valid JSON"

    # Step 3: Use auth cookie to get /auth/session
    try:
        r = requests.get(session_url, cookies=cookies, timeout=TIMEOUT)
        assert r.status_code == 200, f"Expected 200 for authenticated admin session, got {r.status_code}"
        resp_json = r.json()
        # Validate that session info includes expected username or role hints
        assert "username" in resp_json, "Response missing 'username'"
        assert resp_json["username"] in (AUTH_USERNAME, "admin"), "Authenticated username mismatch"
        # Assert presence of any identification field
        assert any(k in resp_json for k in ("id", "userId", "roles", "authorities")), "No session user identification found"
    except requests.RequestException as e:
        assert False, f"Authenticated request to /auth/session failed: {e}"
    except ValueError:
        assert False, f"Response from /auth/session is not valid JSON"

    # Step 4: Call GET /users/admins with auth cookie and confirm only one default admin user present
    users_admins_url = f"{BASE_URL}/users/admins"
    try:
        r = requests.get(users_admins_url, cookies=cookies, timeout=TIMEOUT)
        assert r.status_code == 200, f"Expected 200 for authenticated admin users list, got {r.status_code}"
        admins = r.json()
        assert isinstance(admins, list), "Admins endpoint did not return a list"
        # Check that there is exactly one admin user with username 'admin' or 'admin@pickados.local' or AUTH_USERNAME
        admin_users = [u for u in admins if u.get("username") in ("admin", AUTH_USERNAME, "admin@pickados.local")]
        assert len(admin_users) == 1, f"Expected exactly one default admin user, found {len(admin_users)}"
    except requests.RequestException as e:
        assert False, f"Request to /users/admins failed: {e}"
    except ValueError:
        assert False, f"Response from /users/admins is not valid JSON"

    # Step 5: Call GET /roles with auth cookie and confirm ADMIN role exists at least once
    roles_url = f"{BASE_URL}/roles"
    try:
        r = requests.get(roles_url, cookies=cookies, timeout=TIMEOUT)
        assert r.status_code == 200, f"Expected 200 for authenticated roles list, got {r.status_code}"
        roles = r.json()
        assert isinstance(roles, list), "Roles endpoint did not return a list"
        admin_roles = [role for role in roles if role.get("name", "").upper() == "ADMIN"]
        assert len(admin_roles) >= 1, "ADMIN role not found in roles list"
    except requests.RequestException as e:
        assert False, f"Request to /roles failed: {e}"
    except ValueError:
        assert False, f"Response from /roles is not valid JSON"

test_local_admin_seeder_creation_and_duplication_handling()
