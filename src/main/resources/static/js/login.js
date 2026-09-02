const registerModal = document.getElementById("registerModal");
const registerContent = document.getElementById("registerContent");

function passwordField(id) {
    return `
        <div class="password-wrap">
            <input id="${id}" type="password" minlength="4" required>
            <button
                type="button"
                class="password-toggle"
                data-password="${id}"
                aria-label="Show password">◉</button>
        </div>
    `;
}

function setupPasswordToggle(button) {
    if (!button) return;

    button.onclick = () => {
        const input = document.getElementById(button.dataset.password);
        if (!input) return;

        const showing = input.type === "password";
        input.type = showing ? "text" : "password";
        button.textContent = showing ? "◉" : "◉";
        button.classList.toggle("visible", showing);
        button.setAttribute(
            "aria-label",
            showing ? "Hide password" : "Show password"
        );
    };
}

function registrationForm(type) {
    const customer = type === "customer";

    return `
        <p class="eyebrow">CREATE ACCOUNT</p>
        <h2>${customer ? "Customer registration" : "Restaurant registration"}</h2>
        <p class="modal-subtitle">
            ${customer
                ? "Create your FoodHub customer account."
                : "Create your FoodHub Partner account."}
        </p>

        <form id="registerForm">

            <label>
                Name
                <input id="rName" type="text" placeholder="Enter name" required>
            </label>

            <label>
                Email
                <input id="rEmail" type="email" placeholder="you@example.com" required>
            </label>

            ${customer ? `
                <label>
                    Contact
                    <input id="rContact" type="tel"
                           inputmode="numeric"
                           maxlength="10"
                           placeholder="10 digit number" required>
                </label>

                <label>
                    Address
                    <textarea id="rAddress" rows="2"
                              placeholder="Delivery address"></textarea>
                </label>
            ` : `
                <label>
                    Location
                    <input id="rLocation" type="text"
                           placeholder="Restaurant location" required>
                </label>

                <label>
                    Rating
                    <input id="rRating" type="number"
                           min="1" max="5" step="1" value="5" required>
                </label>
            `}

            <label>
                Password
                ${passwordField("rPassword")}
            </label>

            <button class="btn primary full" type="submit">
                Create account
            </button>
        </form>

        <div id="registerMessage" class="message"></div>
    `;
}

document.querySelectorAll("[data-open]").forEach(button => {
    button.onclick = () => {
        const type = button.dataset.open === "customerRegister"
            ? "customer"
            : "restaurant";

        registerContent.innerHTML = registrationForm(type);
        registerModal.classList.remove("hidden");

        const form = document.getElementById("registerForm");
        setupPasswordToggle(
            form.querySelector("[data-password='rPassword']")
        );

        form.onsubmit = async event => {
            event.preventDefault();

            const message = document.getElementById("registerMessage");
            const name = document.getElementById("rName").value.trim();
            const email = document.getElementById("rEmail").value.trim();
            const password = document.getElementById("rPassword").value;

            try {
                let body;

                if (type === "customer") {
                    body = {
                        name,
                        email,
                        contact: Number(
                            document.getElementById("rContact").value
                        ),
                        address: document.getElementById("rAddress").value.trim(),
                        password
                    };
                } else {
                    body = {
                        name,
                        email,
                        location: document.getElementById("rLocation").value.trim(),
                        rating: Number(
                            document.getElementById("rRating").value
                        ),
                        password
                    };
                }

                await api(
                    type === "customer"
                        ? API.customerRegister
                        : API.restaurantRegister,
                    {
                        method: "POST",
                        body: JSON.stringify(body)
                    }
                );

                setMessage(
                    message,
                    "Account created successfully. You can now sign in.",
                    "success"
                );

                form.reset();

            } catch (error) {
                setMessage(message, error.message);
            }
        };
    };
});

document.getElementById("closeRegister").onclick = () => {
    registerModal.classList.add("hidden");
};

registerModal.onclick = event => {
    if (event.target === registerModal) {
        registerModal.classList.add("hidden");
    }
};

setupPasswordToggle(
    document.getElementById("loginPasswordToggle")
);

document.getElementById("loginForm").onsubmit = async event => {
    event.preventDefault();

    const message = document.getElementById("loginMessage");

    try {
        const response = await api(API.login, {
            method: "POST",
            body: JSON.stringify({
                email: document.getElementById("email").value.trim(),
                password: document.getElementById("password").value,
                role: document.getElementById("role").value
            })
        });

        localStorage.setItem(
            "foodhubUser",
            JSON.stringify(response.data)
        );

        if (response.data.role === "CUSTOMER") {
            location.href = "/customer-dashboard.html";
        } else {
            location.href = "/restaurant-dashboard.html";
        }

    } catch (error) {
        setMessage(message, error.message);
    }
};
