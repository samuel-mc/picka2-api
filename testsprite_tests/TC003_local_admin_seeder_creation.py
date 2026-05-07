import requests
from requests.auth import HTTPBasicAuth

BASE_URL = "http://localhost:8080"
TIMEOUT = 30
AUTH_USERNAME = "john1237"
AUTH_PASSWORD = "123456"

def test_local_admin_seeder_creation():
    session = requests.Session()
    session.auth = HTTPBasicAuth(AUTH_USERNAME, AUTH_PASSWORD)
    headers = {"Accept": "application/json"}

    try:
        # 1. Verify ADMIN role exists or is created (GET /roles)
        roles_resp = session.get(f"{BASE_URL}/roles", headers=headers, timeout=TIMEOUT)
        assert roles_resp.status_code == 200, f"Expected 200 OK for /roles, got {roles_resp.status_code}"
        roles_data = roles_resp.json()
        assert isinstance(roles_data, list), "Roles response should be a list"

        admin_role = next((r for r in roles_data if r.get("name") == "ADMIN"), None)
        assert admin_role is not None, "ADMIN role must exist after seeding when profile=local"

        # 2. Verify default admin user exists with correct properties (GET /users/admins)
        admins_resp = session.get(f"{BASE_URL}/users/admins", headers=headers, timeout=TIMEOUT)
        assert admins_resp.status_code == 200, f"Expected 200 OK for /users/admins, got {admins_resp.status_code}"
        admins_data = admins_resp.json()
        assert isinstance(admins_data, list), "Admins response should be a list"

        # Find admin user by username or email
        admin_user = next(
            (
                u for u in admins_data
                if u.get("username") == "admin" or u.get("email") == "admin@pickados.local"
            ),
            None
        )
        assert admin_user is not None, "Default admin user must exist after seeding"

        # Check user properties
        # Expected: username=admin, email=admin@pickados.local, active=true, deleted=false
        assert admin_user.get("username") == "admin", "Admin username mismatch"
        assert admin_user.get("email") == "admin@pickados.local", "Admin email mismatch"
        assert admin_user.get("active") is True, "Admin user should be active"
        assert admin_user.get("deleted") is False, "Admin user should not be deleted"

        # Since we cannot verify password directly, check a field that shows password is hashed or present
        # Just ensure password field is not returned for security
        assert "password" not in admin_user, "Password field should not be exposed in API response"

        # 3. Restart simulation: Verify no duplicate ADMIN role or admin user created
        # We simulate restart by re-calling the endpoints again to check no duplicates

        roles_resp_2 = session.get(f"{BASE_URL}/roles", headers=headers, timeout=TIMEOUT)
        assert roles_resp_2.status_code == 200
        roles_data_2 = roles_resp_2.json()
        admin_roles = [r for r in roles_data_2 if r.get("name") == "ADMIN"]
        assert len(admin_roles) == 1, "ADMIN role should not be duplicated after restart"

        admins_resp_2 = session.get(f"{BASE_URL}/users/admins", headers=headers, timeout=TIMEOUT)
        assert admins_resp_2.status_code == 200
        admins_data_2 = admins_resp_2.json()
        admin_users = [u for u in admins_data_2 if u.get("username") == "admin" or u.get("email") == "admin@pickados.local"]
        assert len(admin_users) == 1, "Default admin user should not be duplicated after restart"

    finally:
        session.close()

test_local_admin_seeder_creation()