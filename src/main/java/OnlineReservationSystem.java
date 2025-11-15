

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.ResultSet;

public class OnlineReservationSystem extends JFrame {
    private final UserDAO userDAO = new UserDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    public OnlineReservationSystem() {
        showLoginForm();
    }

    private void showLoginForm() {
        setTitle("Login - Online Reservation");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (userDAO.validateUser(user, pass)) {
                JOptionPane.showMessageDialog(this, "Login Successful");
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(new JLabel("Username:"));
        add(userField);
        add(new JLabel("Password:"));
        add(passField);
        add(new JLabel());
        add(loginBtn);

        setVisible(true);
    }

    private void showMainMenu() {
        getContentPane().removeAll();
        setTitle("Online Reservation - Main Menu");
        setLayout(new GridLayout(3, 1, 10, 10));
        setSize(400, 200);

        JButton reserveBtn = new JButton("Make Reservation");
        JButton cancelBtn = new JButton("Cancel Reservation");
        JButton exitBtn = new JButton("Exit");

        reserveBtn.addActionListener(e -> showReservationForm());
        cancelBtn.addActionListener(e -> showCancelForm());
        exitBtn.addActionListener(e -> System.exit(0));

        add(reserveBtn);
        add(cancelBtn);
        add(exitBtn);

        revalidate();
        repaint();
    }

    private void showReservationForm() {
        JFrame form = new JFrame("Reservation Form");
        form.setSize(400, 400);
        form.setLayout(new GridLayout(8, 2, 5, 5));
        form.setLocationRelativeTo(null);

        JTextField name = new JTextField();
        JTextField trainNum = new JTextField();
        JTextField trainName = new JTextField();
        JComboBox<String> classType = new JComboBox<>(new String[]{"Sleeper", "AC", "General"});
        JTextField from = new JTextField();
        JTextField to = new JTextField();
        JTextField date = new JTextField(); // yyyy-MM-dd
        JButton submit = new JButton("Reserve");

        submit.addActionListener(e -> {
        	try {
            String pnr = reservationDAO.addReservation(
                    name.getText(),
                    trainNum.getText(),
                    trainName.getText(),
                    (String) classType.getSelectedItem(),
                    from.getText(),
                    to.getText(),
                    Date.valueOf(date.getText())
            );
            System.out.println("Generated PNR: " + pnr);
            
            if (pnr != null) {
                JOptionPane.showMessageDialog(form, "Reservation Successful!\nPNR: " + pnr);
                form.dispose();
            } else {
                JOptionPane.showMessageDialog(form, "Failed to reserve.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        	}
            catch (Exception ex) {
                ex.printStackTrace(); // ✅ will show what went wrong in the console
                JOptionPane.showMessageDialog(null, "Error: " + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(new JLabel("Passenger Name:")); form.add(name);
        form.add(new JLabel("Train Number:")); form.add(trainNum);
        form.add(new JLabel("Train Name:")); form.add(trainName);
        form.add(new JLabel("Class Type:")); form.add(classType);
        form.add(new JLabel("From:")); form.add(from);
        form.add(new JLabel("To:")); form.add(to);
        form.add(new JLabel("Date (yyyy-MM-dd):")); form.add(date);
        form.add(new JLabel("")); form.add(submit);

        form.setVisible(true);
    }

    private void showCancelForm() {
        JFrame cancel = new JFrame("Cancel Reservation");
        cancel.setSize(400, 250);
        cancel.setLayout(new GridLayout(3, 2, 10, 10));
        cancel.setLocationRelativeTo(null);

        JTextField pnrField = new JTextField();
        JButton search = new JButton("Search");
        JButton cancelBtn = new JButton("Cancel");

        search.addActionListener(e -> {
            try (ResultSet rs = reservationDAO.searchByPNR(pnrField.getText())) {
                if (rs != null && rs.next()) {
                    JOptionPane.showMessageDialog(cancel,
                            "Name: " + rs.getString("passenger_name") +
                                    "\nTrain: " + rs.getString("train_number") + " - " + rs.getString("train_name") +
                                    "\nFrom: " + rs.getString("from_place") + " To: " + rs.getString("to_place") +
                                    "\nDate: " + rs.getDate("date_of_journey"));
                } else {
                    JOptionPane.showMessageDialog(cancel, "PNR not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        cancelBtn.addActionListener(e -> {
            if (reservationDAO.cancelReservation(pnrField.getText())) {
                JOptionPane.showMessageDialog(cancel, "Ticket cancelled successfully!");
                cancel.dispose();
            } else {
                JOptionPane.showMessageDialog(cancel, "PNR not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancel.add(new JLabel("Enter PNR:"));
        cancel.add(pnrField);
        cancel.add(new JLabel(""));
        cancel.add(search);
        cancel.add(new JLabel(""));
        cancel.add(cancelBtn);

        cancel.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OnlineReservationSystem::new);
    }
}
