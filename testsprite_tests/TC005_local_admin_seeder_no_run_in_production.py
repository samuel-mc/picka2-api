import requests
from requests.auth import HTTPBasicAuth

BASE_URL = "http://localhost:8080"
AUTH_USERNAME = "john1237"
AUTH_PASSWORD = "123456"
TIMEOUT = 30

def test_local_admin_seeder_no_run_in_production():
    """
    Test that when the application runs with spring.profiles.active=prod,
    the local admin seeder does not run and no default admin account (username=admin, email=admin@pickados.local) exists.
    """
    # Use basic auth for admin user john1237
    auth = HTTPBasicAuth(AUTH_USERNAME, AUTH_PASSWORD)
    headers = {"Accept": "application/json"}

    try:
        # Query the admins endpoint to list current admins
        resp = requests.get(f"{BASE_URL}/users/admins", auth=auth, headers=headers, timeout=TIMEOUT)
        assert resp.status_code == 200, f"Expected HTTP 200 from /users/admins but got {resp.status_code}"
        admins = resp.json()
        assert isinstance(admins, list), f"Expected JSON list from /users/admins but got {type(admins)}"

        # Check that no admin user with username 'admin' or email 'admin@pickados.local' is present
        for admin in admins:
            username = admin.get("username", "").lower()
            email = admin.get("email", "").lower()
            assert username != "admin", "Default admin user 'admin' should not exist in production profile"
            assert email != "admin@pickados.local", "Default admin email 'admin@pickados.local' should not exist in production profile"
    except requests.RequestException as e:
        assert False, f"Request to list admins failed: {e}"

test_local_admin_seeder_no_run_in_production()