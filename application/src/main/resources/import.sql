-- === Sample Users (schema: users) ===
INSERT INTO users.users (first_name, last_name, email, password, role) VALUES ('Admin', 'User', 'admin@cafefusion.com','$2a$12$DWrRjXKjRuWvHfM6RMd.OOKFfcQPxxODqkgSCoCwS5lKg08QbplWC', 'ADMIN');
INSERT INTO users.users (first_name, last_name, email, password, role) VALUES ('Normal', 'User', 'user@cafefusion.com','$2a$12$yTzOsHTPWiDQbm3.g9XXZeCpIyqxM7VGuvaKsVT8aSmMPzgR3Fuly','USER');


-- === Sample Menu Items (schema: menu) ===
INSERT INTO menu.menu_items (name, description, price) VALUES ('Cappuccino', 'Classic Italian coffee with steamed milk foam.', 12.50);
INSERT INTO menu.menu_items (name, description, price) VALUES ('Baklava', 'Sweet Turkish dessert made of filo pastry, nuts, and syrup.', 18.00);
INSERT INTO menu.menu_items (name, description, price) VALUES ('Espresso', 'A strong, full-bodied coffee shot.', 9.00);
INSERT INTO menu.menu_items (name, description, price) VALUES ('Cheesecake', 'Creamy New York style cheesecake with a berry topping.', 22.00);
INSERT INTO menu.menu_items (name, description, price) VALUES ('Iced Latte', 'Chilled espresso with milk over ice.', 14.00);

-- === Sample Events (schema: events) ===
INSERT INTO events.events (name, description, event_date_time, cover_charge) VALUES ('Sunday EDM Session', 'Live DJ set by DJ R-Vibe. Enjoy deep house and techno.', '2025-12-07T18:00:00+01:00', 25.00);
INSERT INTO events.events (name, description, event_date_time, cover_charge) VALUES ('Acoustic Evening', 'A relaxed Sunday evening with live acoustic guitar.', '2025-12-14T19:00:00+01:00', 0.00);