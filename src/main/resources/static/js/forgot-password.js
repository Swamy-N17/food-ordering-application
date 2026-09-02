const form = document.getElementById("forgotPasswordForm");
const message = document.getElementById("forgotMessage");

form.onsubmit = async function(event) {

    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const role = document.getElementById("role").value;

    try {

        setMessage(message, "Sending reset link...", "success");

        await api("/auth/forgot-password", {
            method: "POST",
            body: JSON.stringify({
                email: email,
                role: role
            })
        });

        setMessage(
            message,
            "Password reset link has been sent to your email.",
            "success"
        );

        form.reset();

    } catch (error) {

        setMessage(
            message,
            error.message
        );
    }
};