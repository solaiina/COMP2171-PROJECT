const wrapper = document.getElementById("authWrapper");
const showRegister = document.getElementById("showRegister");
const showLogin = document.getElementById("showLogin");
const closeBtn = document.getElementById("closeBtn");
const toast = document.getElementById("toast");

const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");

showRegister.addEventListener("click", (e) => {
    e.preventDefault();
    wrapper.classList.add("active");
});

showLogin.addEventListener("click", (e) => {
    e.preventDefault();
    wrapper.classList.remove("active");
});

closeBtn.addEventListener("click", () => {
    window.location.href = "index.html";
});

function showToast(message) {
    toast.textContent = message;
    toast.classList.remove("hidden");
    setTimeout(() => toast.classList.add("hidden"), 3000);
}

function setError(id, message) {
    document.getElementById(id).textContent = message;
}

function clearErrors() {
    document.querySelectorAll(".error").forEach(el => el.textContent = "");
}

function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function togglePassword(targetId, iconElement) {
    const input = document.getElementById(targetId);
    const icon = iconElement.querySelector("i");

    if (input.type === "password") {
        input.type = "text";
        icon.className = "fa fa-unlock-alt";
    } else {
        input.type = "password";
        icon.className = "fa fa-lock";
    }
}

document.querySelectorAll(".toggle-password").forEach(icon => {
    icon.addEventListener("click", () => {
        togglePassword(icon.dataset.target, icon);
    });
});

function getUsers() {
    return JSON.parse(localStorage.getItem("call_users")) || [];
}

function saveUsers(users) {
    localStorage.setItem("call_users", JSON.stringify(users));
}

registerForm.addEventListener("submit", (e) => {
    e.preventDefault();
    clearErrors();

    const firstName = document.getElementById("firstName").value.trim();
    const lastName = document.getElementById("lastName").value.trim();
    const birthDate = document.getElementById("birthDate").value;
    const email = document.getElementById("registerEmail").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const role = document.getElementById("userRole").value;
    const password = document.getElementById("registerPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const termsChecked = document.getElementById("termsCheckbox").checked;

    let valid = true;

    if (!firstName) {
        setError("firstNameError", "First name is required");
        valid = false;
    }

    if (!lastName) {
        setError("lastNameError", "Last name is required");
        valid = false;
    }

    if (!birthDate) {
        setError("birthDateError", "Birth date is required");
        valid = false;
    }

    if (!email) {
        setError("registerEmailError", "Email is required");
        valid = false;
    } else if (!isValidEmail(email)) {
        setError("registerEmailError", "Invalid email");
        valid = false;
    }

    if (!phone) {
        setError("phoneError", "Phone number is required");
        valid = false;
    } else if (!/^\d{10}$/.test(phone)) {
        setError("phoneError", "Phone number must be 10 digits");
        valid = false;
    }

    if (!role) {
        setError("userRoleError", "Please select a role");
        valid = false;
    }

    if (!password) {
        setError("registerPasswordError", "Password is required");
        valid = false;
    } else if (password.length < 8) {
        setError("registerPasswordError", "Password must be at least 8 characters");
        valid = false;
    }

    if (!confirmPassword) {
        setError("confirmPasswordError", "Confirm password is required");
        valid = false;
    } else if (password !== confirmPassword) {
        setError("confirmPasswordError", "Passwords do not match");
        valid = false;
    }

    if (!termsChecked) {
        showToast("You must accept the terms and conditions");
        valid = false;
    }

    if (!valid) return;

    const users = getUsers();

    const existingUser = users.find(user => user.email === email);
    if (existingUser) {
        showToast("That email is already registered");
        return;
    }

    users.push({
        firstName,
        lastName,
        birthDate,
        email,
        phone,
        role,
        password
    });

    saveUsers(users);

    document.getElementById("loginEmail").value = email;
    document.getElementById("loginPassword").value = password;

    registerForm.reset();
    wrapper.classList.remove("active");
    showToast("Registration successful. Please log in.");
});

loginForm.addEventListener("submit", (e) => {
    e.preventDefault();
    clearErrors();

    const email = document.getElementById("loginEmail").value.trim();
    const password = document.getElementById("loginPassword").value;
    const remember = document.getElementById("rememberCheckbox").checked;

    let valid = true;

    if (!email) {
        setError("loginEmailError", "Email is required");
        valid = false;
    } else if (!isValidEmail(email)) {
        setError("loginEmailError", "Invalid email");
        valid = false;
    }

    if (!password) {
        setError("loginPasswordError", "Password is required");
        valid = false;
    }

    if (!valid) return;

    const users = getUsers();
    const user = users.find(u => u.email === email && u.password === password);

    if (!user) {
        showToast("Invalid email or password");
        return;
    }

    if (remember) {
        localStorage.setItem("remember_email", email);
        localStorage.setItem("remember_password", password);
        localStorage.setItem("remember_checked", "true");
    } else {
        localStorage.removeItem("remember_email");
        localStorage.removeItem("remember_password");
        localStorage.removeItem("remember_checked");
    }

    localStorage.setItem("logged_in_user", JSON.stringify(user));
    showToast("Login successful");

    setTimeout(() => {
        if (user.role === "provider") {
            window.location.href = "pages/provider-dashboard.html";
        } else {
            alert("Client login successful. Client page can be built next.");
        }
    }, 1000);
});

function loadRememberedLogin() {
    const rememberChecked = localStorage.getItem("remember_checked");
    if (rememberChecked === "true") {
        document.getElementById("loginEmail").value = localStorage.getItem("remember_email") || "";
        document.getElementById("loginPassword").value = localStorage.getItem("remember_password") || "";
        document.getElementById("rememberCheckbox").checked = true;
    }
}

document.getElementById("phone").addEventListener("keydown", (event) => {
    const allowed =
        (event.key >= "0" && event.key <= "9") ||
        event.key === "Backspace" ||
        event.key === "Tab" ||
        event.key === "ArrowLeft" ||
        event.key === "ArrowRight" ||
        event.key === "Delete";

    if (!allowed) {
        event.preventDefault();
    }
});

loadRememberedLogin();