-- === Sample Users (schema: users) ===
INSERT INTO users.users (first_name, last_name, email, password, role) VALUES ('Admin', 'User', 'admin@cafefusion.com','$2a$12$DWrRjXKjRuWvHfM6RMd.OOKFfcQPxxODqkgSCoCwS5lKg08QbplWC', 'ADMIN');
INSERT INTO users.users (first_name, last_name, email, password, role) VALUES ('Normal', 'User', 'user@cafefusion.com','$2a$12$yTzOsHTPWiDQbm3.g9XXZeCpIyqxM7VGuvaKsVT8aSmMPzgR3Fuly','USER');


-- === Sample Menu Items (schema: menu) ===
INSERT INTO menu.menu_items (name, description, price) VALUES ('Cappuccino', 'Classic Italian coffee with steamed milk foam.', 12.50);
INSERT INTO menu.menu_items (name, description, price) VALUES ('Baklava', 'Sweet Turkish dessert made of filo pastry, nuts, and syrup.', 18.00);
INSERT INTO menu.menu_items (name, description, price) VALUES ('Espresso', 'A strong, full-bodied coffee shot.', 9.00);
INSERT INTO menu.menu_items (name, description, price) VALUES ('Cheesecake', 'Creamy New York style cheesecake with a berry topping.', 22.00);

-- === Sample Events (schema: events) ===
INSERT INTO events.events (name, description, event_date_time, cover_charge) VALUES ('Sunday EDM Session', 'Live DJ set by DJ R-Vibe.', '2025-12-07T18:00:00+01:00', 25.00);

-- === Sample Orders (schema: orders) ===
-- Order 1: Placed by the USER (userId = 2)
INSERT INTO orders.orders (user_id, created_at, total_price, status) VALUES (2, '2025-10-28T10:00:00Z', 30.50, 'PENDING_APPROVAL');
-- Order 2: Placed by the ADMIN (userId = 1)
INSERT INTO orders.orders (user_id, created_at, total_price, status) VALUES (1, '2025-10-29T11:00:00Z', 9.00, 'CONFIRMED');

-- === Sample Order Items (schema: orders) ===
-- Items for Order 1 (Cappuccino and Baklava)
INSERT INTO orders.order_items (order_id, menu_item_id, name, price) VALUES (1, 1, 'Cappuccino', 12.50);
INSERT INTO orders.order_items (order_id, menu_item_id, name, price) VALUES (1, 2, 'Baklava', 18.00);
-- Item for Order 2 (Espresso)
INSERT INTO orders.order_items (order_id, menu_item_id, name, price) VALUES (2, 3, 'Espresso', 9.00);