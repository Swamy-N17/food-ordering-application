const user = requireUser("CUSTOMER");

if (user) {
    renderNavbar("CUSTOMER");
}

let cart = getCart();

function renderCart() {
    const container = document.getElementById("cartItems");

    if (!cart.length) {
        container.innerHTML = `
            <div class="empty cart-empty">
                <div class="empty-icon">🛒</div>
                <h3>Your cart is empty</h3>
                <p class="muted">
                    Pick something delicious from a restaurant.
                </p>
                <a class="btn primary" href="/customer-dashboard.html">
                    Browse restaurants
                </a>
            </div>
        `;

        updateSummary(0, 0);
        return;
    }

    container.innerHTML = cart.map((item, index) => `
        <article class="cart-row">

            <div class="cart-visual">
                ${foodVisual(item.itemName)}
            </div>

            <div class="cart-info">
                <h3>${escapeHtml(item.itemName)}</h3>
                <small>${money(item.price)} each</small>
            </div>

            <div class="cart-actions">
                <button onclick="changeQuantity(${index}, -1)">−</button>
                <strong>${item.quantity}</strong>
                <button onclick="changeQuantity(${index}, 1)">+</button>
                <button class="remove-btn"
                        onclick="removeItem(${index})">Remove</button>
            </div>

            <strong class="cart-price">
                ${money(item.price * item.quantity)}
            </strong>
        </article>
    `).join("");

    const count = cart.reduce(
        (sum, item) => sum + Number(item.quantity),
        0
    );

    const total = cart.reduce(
        (sum, item) => sum + item.price * item.quantity,
        0
    );

    updateSummary(count, total);
}

function updateSummary(count, total) {
    document.getElementById("summaryItems").textContent = count;
    document.getElementById("summaryTotal").textContent = money(total);

    const button = document.getElementById("checkoutBtn");
    button.disabled = count === 0;
}

window.changeQuantity = function(index, change) {
    cart[index].quantity += change;

    if (cart[index].quantity < 1) {
        cart.splice(index, 1);
    }

    saveCart(cart);
    renderCart();
};

window.removeItem = function(index) {
    cart.splice(index, 1);
    saveCart(cart);
    renderCart();
};

document.getElementById("checkoutBtn").onclick = () => {
    if (!cart.length) return;
    location.href = "/checkout.html";
};

renderCart();
