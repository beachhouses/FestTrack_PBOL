import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePicker {
    private int month = Calendar.getInstance().get(Calendar.MONTH);
    private int year = Calendar.getInstance().get(Calendar.YEAR);
    private JLabel lblMonth, lblYear;
    private JButton[] btnDays = new JButton[42];
    private JDialog dialog;
    private Date selectedDate;
    private Date today = new Date();
    private Calendar current = Calendar.getInstance();

    public DatePicker(JFrame parent) {
        dialog = new JDialog(parent, "Pilih Tanggal", true);
        dialog.setSize(310, 280);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        lblMonth = new JLabel();
        lblYear = new JLabel();
        lblMonth.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblYear.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        header.add(prev);
        header.add(lblMonth);
        header.add(lblYear);
        header.add(next);
        dialog.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(7, 7, 2, 2));
        String[] days = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(new Color(30, 50, 100));
            grid.add(lbl);
        }

        for (int i = 0; i < 42; i++) {
            btnDays[i] = new JButton("");
            btnDays[i].setFocusPainted(false);
            btnDays[i].setBackground(Color.WHITE);
            btnDays[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnDays[i].setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            int idx = i;
            btnDays[i].addActionListener(e -> {
                String day = btnDays[idx].getText();
                if (!day.isEmpty()) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, Integer.parseInt(day));
                    selectedDate = cal.getTime();
                    dialog.dispose();
                }
            });
            grid.add(btnDays[i]);
        }
        dialog.add(grid, BorderLayout.CENTER);

        prev.addActionListener(e -> { month--; if (month < 0) { month = 11; year--; } updateCalendar(); });
        next.addActionListener(e -> { month++; if (month > 11) { month = 0; year++; } updateCalendar(); });

        updateCalendar();
    }

    private void updateCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        int startDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        lblMonth.setText(new SimpleDateFormat("MMMM").format(cal.getTime()));
        lblYear.setText(String.valueOf(year));

        for (JButton b : btnDays) {
            b.setText("");
            b.setBackground(Color.WHITE);
            b.setForeground(Color.BLACK);
        }

        Calendar todayCal = Calendar.getInstance();
        int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
        int todayMonth = todayCal.get(Calendar.MONTH);
        int todayYear = todayCal.get(Calendar.YEAR);

        for (int i = 0; i < daysInMonth; i++) {
            JButton btn = btnDays[startDay + i];
            btn.setText(String.valueOf(i + 1));

            // highlight today
            if (year == todayYear && month == todayMonth && (i + 1) == todayDay) {
                btn.setBackground(new Color(170, 200, 255));
                btn.setForeground(Color.BLACK);
            }

            // hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!btn.getText().isEmpty()) btn.setBackground(new Color(90, 140, 255));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!btn.getText().isEmpty()) {
                        if (year == todayYear && month == todayMonth && btn.getText().equals(String.valueOf(todayDay)))
                            btn.setBackground(new Color(170, 200, 255));
                        else btn.setBackground(Color.WHITE);
                    }
                }
            });
        }
    }

    public Date pickDate() {
        dialog.setVisible(true);
        return selectedDate;
    }
}