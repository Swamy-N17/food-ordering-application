const API = {
    login: "/auth/login",
    customerRegister: "/auth/customer/register",
    restaurantRegister: "/auth/restaurant/register",

    restaurants: "/restaurant/all",
    restaurant: (id) => `/restaurant/${id}`,
    restaurantMenu: (id) => `/restaurant/menuitem/${id}`,

    addMenu: "/menuitem",
    updateMenu: (id) => `/menuitem/${id}`,

    placeOrder: "/order/place",
    customerOrders: (id) => `/order/customer/${id}`,
    order: (id) => `/order/${id}`,
    restaurantOrders: (id) => `/order/restaurant/${id}`,
    updateOrder: (id) => `/order/${id}/status`,
    cancelOrder: (id) => `/order/${id}/cancel`,

    customer: (id) => `/customer/${id}`,
    updateCustomer: (id) => `/customer/${id}`,
    updateRestaurant: (id) => `/restaurant/update/${id}`,

    paymentByOrder: (id) => `/payment/order/${id}`,
    updatePayment: (id) => `/payment/update/${id}`
};

function getUser() {
    try {
        return JSON.parse(localStorage.getItem("foodhubUser"));
    } catch (e) {
        return null;
    }
}

function requireUser(role) {
    const user = getUser();

    if (!user || user.role !== role) {
        location.href = "/";
        return null;
    }

    return user;
}

function logout() {
    localStorage.removeItem("foodhubUser");
    localStorage.removeItem("foodhubCart");
    location.href = "/";
}

function api(url, options = {}) {
    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {})
    };

    return fetch(url, { ...options, headers })
        .then(async response => {
            let body = {};

            try {
                body = await response.json();
            } catch (e) {
                body = {};
            }

            if (!response.ok) {
                const error = new Error(
                    body.message || `Request failed (${response.status})`
                );
                error.status = response.status;
                throw error;
            }

            return body;
        });
}

function setMessage(element, text, type = "error") {
    if (!element) return;

    element.textContent = text;
    element.className = `message ${type}`;
}

function money(value) {
    return `₹${Number(value || 0).toFixed(2)}`;
}

function escapeHtml(value = "") {
    return String(value).replace(/[&<>'"]/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        "'": "&#39;",
        '"': "&quot;"
    }[character]));
}

function orderStatusClass(status) {
    return String(status || "").toLowerCase().replaceAll("_", "-");
}

function getCart() {
    try {
        return JSON.parse(localStorage.getItem("foodhubCart")) || [];
    } catch (e) {
        return [];
    }
}

function saveCart(cart) {
    localStorage.setItem("foodhubCart", JSON.stringify(cart));
    updateCartCount();
}

function updateCartCount() {
    const element = document.getElementById("cartCount");

    if (element) {
        element.textContent = getCart()
            .reduce((total, item) => total + Number(item.quantity || 0), 0);
    }
}

/* Food Icons*/
function foodVisual(itemName = "") {

    const name = itemName.toLowerCase();

    // Indian food
    if (name.includes("biryani")) return "🍛";
    if (name.includes("dosa")) return "🥞";
    if (name.includes("idli")) return "🥟";
    if (name.includes("vada")) return "🥯";
    if (name.includes("paratha")) return "🫓";
    if (name.includes("roti") || name.includes("chapati")) return "🫓";
    if (name.includes("puri")) return "🥯";
    if (name.includes("samosa")) return "🥟";
    if (name.includes("pakora") || name.includes("pakoda")) return "🍘";
    if (name.includes("paneer") || name.includes("panner")) return "🥘";
    if (name.includes("curry")) return "🍲";
    if (name.includes("dal")) return "🍲";

    // Rice / noodles
    if (name.includes("fried rice")) return "🍚";
    if (name.includes("rice")) return "🍚";
    if (name.includes("noodle")) return "🍜";
    if (name.includes("pasta")) return "🍝";

    // Fast food
    if (name.includes("pizza")) return "🍕";
    if (name.includes("burger")) return "🍔";
    if (name.includes("sandwich")) return "🥪";
    if (name.includes("fries") || name.includes("french fry")) return "🍟";
    if (name.includes("hot dog")) return "🌭";

    // Meat / non-veg
    if (name.includes("chicken")) return "🍗";
    if (name.includes("mutton")) return "🍖";
    if (name.includes("fish")) return "🐟";
    if (name.includes("prawn") || name.includes("shrimp")) return "🍤";
    if (name.includes("egg")) return "🥚";

    // Desserts
    if (name.includes("ice cream") || name.includes("icecream")) return "🍦";
    if (name.includes("cake")) return "🍰";
    if (name.includes("donut") || name.includes("doughnut")) return "🍩";
    if (name.includes("chocolate")) return "🍫";
    if (name.includes("sweet")) return "🍬";
    if (name.includes("gulab jamun")) return "🍩";

    // Drinks
    if (name.includes("juice")) return "🥤";
    if (name.includes("milkshake")) return "🥤";
    if (name.includes("smoothie")) return "🥤";
    if (name.includes("coffee")) return "☕";
    if (name.includes("tea")) return "🍵";
    if (name.includes("lassi")) return "🥛";
    if (name.includes("shake")) return "🥤";

    // Generic fallback for any future food
    return "🍽️";
}

function renderNavbar(role) {
    const header = document.getElementById("appHeader");

    if (!header) return;

    if (role === "CUSTOMER") {
        header.innerHTML = `
            <header class="topbar">
                <a class="brand" href="/customer-dashboard.html">
                    <span class="brand-mark">F</span>
                    <span>FoodHub</span>
                </a>

                <nav class="nav-links">
                    <a href="/customer-dashboard.html">Home</a>
                    <a href="/my-orders.html">My Orders</a>
                    <a class="cart-link" href="/cart.html">
                        Cart <span id="cartCount">0</span>
                    </a>
                    <a href="/customer-profile.html">Profile</a>
                    <button class="nav-button" id="logoutBtn">Logout</button>
                </nav>
            </header>
        `;
    }

    if (role === "RESTAURANT") {
        header.innerHTML = `
            <header class="topbar">
                <a class="brand" href="/restaurant-dashboard.html">
                    <span class="brand-mark">F</span>
                    <span>FoodHub Partner</span>
                </a>

                <nav class="nav-links">
                    <a href="/restaurant-dashboard.html">Dashboard</a>
                    <a href="/restaurant-dashboard.html#menu">Menu</a>
                    <a href="/restaurant-dashboard.html#orders">Orders</a>
                    <a href="/restaurant-profile.html">Profile</a>
                    <button class="nav-button" id="logoutBtn">Logout</button>
                </nav>
            </header>
        `;
    }

    const logoutButton = document.getElementById("logoutBtn");
    if (logoutButton) logoutButton.onclick = logout;

    updateCartCount();
}

document.addEventListener("DOMContentLoaded", updateCartCount);
