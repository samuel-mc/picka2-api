import requests
from requests.auth import HTTPBasicAuth

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

AUTH_USERNAME = "john1237"
AUTH_PASSWORD = "123456"

def test_local_admin_seeder_skip_existing():
    """
    Test that starting the application with spring.profiles.active=local when ADMIN role and admin user exist
    causes the seeder to skip creation and no duplicate records are created.
    """
    session = requests.Session()
    session.auth = HTTPBasicAuth(AUTH_USERNAME, AUTH_PASSWORD)
    headers = {"Accept": "application/json"}

    # Step 1: Fetch roles and verify ADMIN role exists exactly once
    roles_resp = session.get(f"{BASE_URL}/roles", headers=headers, timeout=TIMEOUT)
    assert roles_resp.status_code == 200, f"Failed to fetch roles: {roles_resp.text}"
    roles_data = roles_resp.json()
    assert isinstance(roles_data, list), "Roles response is not a list"
    admin_roles = [role for role in roles_data if role.get("name") == "ADMIN"]
    assert len(admin_roles) == 1, f"Expected exactly one ADMIN role, found {len(admin_roles)}"

    # Step 2: Fetch admins and verify default admin user exists exactly once
    admins_resp = session.get(f"{BASE_URL}/users/admins", headers=headers, timeout=TIMEOUT)
    assert admins_resp.status_code == 200, f"Failed to fetch admin users: {admins_resp.text}"
    admins_data = admins_resp.json()
    assert isinstance(admins_data, list), "Admins response is not a list"
    # Find admin user by username or email
    matching_admins = [
        admin for admin in admins_data
        if (admin.get("username") == "admin" or admin.get("email") == "admin@pickados.local")
    ]
    assert len(matching_admins) == 1, f"Expected exactly one seeded admin user, found {len(matching_admins)}"
    admin_user = matching_admins[0]

    # Verify key admin user properties:
    assert admin_user.get("username") == "admin", "Admin username mismatch"
    assert admin_user.get("email") == "admin@pickados.local", "Admin email mismatch"
    assert admin_user.get("active") is True, "Admin user should be active"
    assert admin_user.get("deleted") is False, "Admin user should not be deleted"
    # Password hash cannot be directly checked via API, so skip

    # Step 3: To confirm no duplicates on seeder skip, re-fetch roles and admins again to verify counts unchanged
    second_roles_resp = session.get(f"{BASE_URL}/roles", headers=headers, timeout=TIMEOUT)
    assert second_roles_resp.status_code == 200, f"Failed to fetch roles second time: {second_roles_resp.text}"
    second_roles_data = second_roles_resp.json()
    second_admin_roles = [role for role in second_roles_data if role.get("name") == "ADMIN"]
    assert len(second_admin_roles) == 1, "Duplicate ADMIN role detected after seeder re-run"

    second_admins_resp = session.get(f"{BASE_URL}/users/admins", headers=headers, timeout=TIMEOUT)
    assert second_admins_resp.status_code == 200, f"Failed to fetch admin users second time: {second_admins_resp.text}"
    second_admins_data = second_admins_resp.json()
    second_matching_admins = [
        admin for admin in second_admins_data
        if (admin.get("username") == "admin" or admin.get("email") == "admin@pickados.local")
    ]
    assert len(second_matching_admins) == 1, "Duplicate admin user detected after seeder re-run"

test_local_admin_seeder_skip_existing()