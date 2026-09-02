const user = requireUser("CUSTOMER");

if (user) {
    renderNavbar("CUSTOMER");
}

async function loadOrders() {

    const list = document.getElementById("ordersList");
    const message = document.getElementById("ordersMessage");

    try {

        const response = await api(API.customerOrders(user.id));

        const orders = response.data || [];

        if (!orders.length) {
            renderEmpty(list);
            return;
        }

        list.innerHTML = orders
            .slice()
            .reverse()
            .map(renderOrder)
            .join("");

    } catch (error) {

        if (error.status === 404) {
            renderEmpty(list);
        } else {
            setMessage(message, error.message);
        }
    }
}


function renderEmpty(element) {

    element.innerHTML = `

        <div class="empty">

            <div class="empty-icon">📦</div>

            <h3>No orders yet</h3>

            <p class="muted">
                Your placed orders will appear here.
            </p>

            <a class="btn primary"
               href="/customer-dashboard.html">
                Order food
            </a>

        </div>

    `;
}


function renderOrder(order) {

    const canCancel =
        ["PLACED", "CONFIRMED", "PREPARING"]
            .includes(order.status);

    // Format order date and time
    const orderDate = order.orderDateTime
        ? new Date(order.orderDateTime).toLocaleString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        })
        : "Date not available";

    return `

        <article class="order-card">

            <div class="order-top">

                <div>

                    <p class="eyebrow">ORDER</p>

                    <h3>#${order.orderId}</h3>

                    <div class="order-meta">
                        📅 Placed on: ${orderDate}
                    </div>

                </div>

                <span class="status ${orderStatusClass(order.status)}">

                    ${order.status || "-"}

                </span>

            </div>


            <div class="delivery-summary">
                <div>
                    <span class="delivery-label">DELIVERY ADDRESS</span>
                    <strong>📍 ${escapeHtml(order.deliveryAddress || order.customer?.address || "Address not available")}</strong>
                </div>
            </div>

            <div class="order-items">

                ${(order.orderItems || []).map(item => `

                    <div class="order-item">

                        <span>

                            ${escapeHtml(
                                item.menuItem?.itemName || "Item"
                            )}

                            × ${item.quantity}

                        </span>

                        <strong>
                            ${money(item.subTotal)}
                        </strong>

                    </div>

                `).join("")}

            </div>


            <div class="order-bottom">

                <span>

                    💳 Payment:
                    ${order.payment?.paymentMethod || "-"}

                    ·

                    ${order.payment?.paymentStatus || "-"}

                </span>

                <strong class="order-total">

                    ${money(order.totalAmount)}

                </strong>

            </div>


            ${
                canCancel
                ? `

                    <button
                        class="btn danger"
                        onclick="cancelOrder(${order.orderId})">

                        Cancel order

                    </button>

                  `
                : ""
            }

        </article>

    `;
}


window.cancelOrder = async function(orderId) {

    if (!confirm("Cancel this order?"))
        return;

    try {

        await api(API.cancelOrder(orderId), {
            method: "PATCH"
        });

        loadOrders();

    } catch (error) {

        setMessage(
            document.getElementById("ordersMessage"),
            error.message
        );
    }
};


loadOrders();