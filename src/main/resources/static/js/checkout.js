const user = requireUser("CUSTOMER");

if (user) {
    renderNavbar("CUSTOMER");
}

const cart = getCart();

if (!cart.length) {
    location.href = "/cart.html";
}

const total = cart.reduce(
    (sum, item) => sum + Number(item.price) * Number(item.quantity),
    0
);

const checkoutTotal = document.getElementById("checkoutTotal");
const checkoutItems = document.getElementById("checkoutItems");
const customerName = document.getElementById("customerName");
const customerContact = document.getElementById("customerContact");
const customerAddress = document.getElementById("customerAddress");
const addressMessage = document.getElementById("addressMessage");

checkoutTotal.textContent = money(total);

checkoutItems.innerHTML = cart.map(item => `
    <div class="mini-item">
        <div class="mini-item-info">
            <span class="mini-food">${foodVisual(item.itemName)}</span>
            <span>
                <b>${escapeHtml(item.itemName)}</b>
                <small>× ${item.quantity}</small>
            </span>
        </div>
        <strong>${money(item.price * item.quantity)}</strong>
    </div>
`).join("");

async function loadCustomerDetails() {
    try {
        const response = await api(API.customer(user.id));
        const customer = response.data;

        customerName.textContent = customer.name || "Customer";
        customerContact.textContent = customer.contact || "-";
        customerAddress.textContent = customer.address || "Address not added";

        if (!customer.address || !customer.address.trim()) {
            addressMessage.innerHTML = `
                Please add your delivery address from
                <a href="/customer-profile.html">Profile</a>
                before placing an order.
            `;
            addressMessage.classList.add("warning");
        }
    } catch (error) {
        setMessage(addressMessage, error.message);
    }
}

function updatePaymentText() {
    const selected = document.querySelector("input[name='method']:checked");
    if (!selected) return;

    const method = selected.value;
    const note = document.getElementById("paymentNote");

    if (method === "CASH") {
        note.textContent =
            "Cash on delivery: payment will remain PENDING.";
    } else {
        note.textContent =
            "Demo payment: no real money will be charged. " +
            "The payment will be marked SUCCESS.";
    }

    document.getElementById("placeOrderBtn").textContent =
        method === "CASH"
            ? "Place Order"
            : `Pay ${money(total)} & Place Order`;
}

document.querySelectorAll("input[name='method']")
    .forEach(input => input.onchange = updatePaymentText);

updatePaymentText();

async function placeOrder() {
    const message = document.getElementById("checkoutMessage");
    const button = document.getElementById("placeOrderBtn");
    const selected = document.querySelector("input[name='method']:checked");

    if (!selected) {
        setMessage(message, "Please select a payment method.");
        return;
    }

    const method = selected.value;

    button.disabled = true;
    button.textContent = "Processing...";

    try {
        // Get the latest customer details before creating the order.
        const customerResponse = await api(API.customer(user.id));
        const customer = customerResponse.data;

        if (!customer.address || !customer.address.trim()) {
            setMessage(
                message,
                "Delivery address is required. Please update your Profile."
            );
            button.disabled = false;
            updatePaymentText();
            return;
        }

        const paymentStatus =
            method === "CASH" ? "PENDING" : "SUCCESS";

        const order = {
            customer: {
                customerId: user.id
            },

            orderItems: cart.map(item => ({
                quantity: Number(item.quantity),
                menuItem: {
                    menuItemId: item.menuItemId
                }
            })),

            payment: {
                paymentMethod: method,
                paymentStatus,
                amount: total
            }
        };

        const response = await api(API.placeOrder, {
            method: "POST",
            body: JSON.stringify(order)
        });

        localStorage.removeItem("foodhubCart");

        const placedOrder = response.data;
        const deliveryAddress =
            placedOrder.deliveryAddress || customer.address;

        document.querySelector(".checkout-card").innerHTML = `
            <div class="success-screen">
                <div class="success-icon">✓</div>

                <p class="eyebrow">ORDER CONFIRMED</p>

                <h2>Order #${placedOrder.orderId} placed!</h2>

                <p class="muted">
                    ${method === "CASH"
                        ? "Your order is confirmed. Pay when it arrives."
                        : "Your demo payment was successful."}
                </p>

                <div class="delivery-card">
                    <p class="eyebrow">DELIVERY DETAILS</p>
                    <strong>${escapeHtml(customer.name || "Customer")}</strong>
                    <span>📞 ${escapeHtml(String(customer.contact || "-"))}</span>
                    <span>📍 ${escapeHtml(deliveryAddress)}</span>
                </div>

                <div class="success-details">
                    <span>Payment</span>
                    <strong>${paymentStatus}</strong>
                    <span>Total</span>
                    <strong>${money(total)}</strong>
                </div>

                <a class="btn primary" href="/my-orders.html">
                    View my orders
                </a>
            </div>
        `;

    } catch (error) {
        setMessage(message, error.message);
        button.disabled = false;
        updatePaymentText();
    }
}

document.getElementById("placeOrderBtn").onclick = placeOrder;

loadCustomerDetails();
