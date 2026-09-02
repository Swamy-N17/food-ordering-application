const form = document.getElementById("resetPasswordForm");
const message = document.getElementById("resetMessage");

const params = new URLSearchParams(location.search);
const token = params.get("token");

const newPassword = document.getElementById("newPassword");
const confirmPassword = document.getElementById("confirmPassword");

function setupPasswordToggle(buttonId, inputId) {

    const button = document.getElementById(buttonId);
    const input = document.getElementById(inputId);

    if (!button || !input) return;

    button.onclick = () => {

        const show = input.type === "password";

        input.type = show ? "text" : "password";

        button.textContent = show ? "Hide" : "Show";

    };
}

setupPasswordToggle(
    "newPasswordToggle",
    "newPassword"
);

setupPasswordToggle(
    "confirmPasswordToggle",
    "confirmPassword"
);

if (!token) {

    setMessage(
        message,
        "Invalid password reset link."
    );

    form.querySelector("button[type='submit']").disabled = true;
}

form.onsubmit = async function(event) {

    event.preventDefault();

    if (!token) {

        setMessage(
            message,
            "Invalid password reset link."
        );

        return;
    }

    const password = newPassword.value;
    const confirmation = confirmPassword.value;

    if (password !== confirmation) {

        setMessage(
            message,
            "Passwords do not match."
        );

        return;
    }

    if (password.length < 4) {

        setMessage(
            message,
            "Password must contain at least 4 characters."
        );

        return;
    }

    try {

        await api("/auth/reset-password", {

            method: "POST",

            body: JSON.stringify({

                token: token,

                newPassword: password

            })

        });

        setMessage(
            message,
            "Password reset successfully. You can now login.",
            "success"
        );

        form.reset();

        setTimeout(() => {

            location.href = "/";

        }, 2000);

    } catch (error) {

        setMessage(
            message,
            error.message
        );
    }
};