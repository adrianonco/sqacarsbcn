package CarsProjectDev;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * The panel used for adding cars to the CarSalesSystem
 * @
 *
 * PUBLIC FEATURES:
 * // Constructors
 *    public AddCarPanel(CarSalesSystem carSys, JPanel dest)
 *
 * // Methods
 *    public void actionPerformed(ActionEvent ev)
 *
 * COLLABORATORS:
 *    CarDetailComponents
 *
 * @version 1.0, 16 Oct 2004
 * @author Adam Black
 */
public class AddCarPanel extends JPanel implements ActionListener
{
	private CarSalesSystem carSystem;
	private JLabel headingLabel = new JLabel("Add a Car");
	private JButton resetButton = new JButton("Reset");
	private JButton saveButton = new JButton("Save");
	private JPanel buttonPanel = new JPanel();
	private CarDetailsComponents carComponents = new CarDetailsComponents();

	// Constant for repeated literal
    private static final String INVALID_FIELD_MESSAGE = "Invalid field";
    
	/**
	 * @param carSys links to a CarSalesSystem object
	 * @param dest where the components will be placed
	 */
	public AddCarPanel(CarSalesSystem carSys)
	{
		carSystem = carSys;

		resetButton.addActionListener(this);
		saveButton.addActionListener(this);
		headingLabel.setAlignmentX(0.5f);

		buttonPanel.add(resetButton);
		buttonPanel.add(saveButton);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(Box.createVerticalStrut(10));
		add(headingLabel);
		add(Box.createVerticalStrut(15));
		carComponents.add(buttonPanel, "Center");
		add(carComponents);
	}

	/**
	 * check which buttons were pressed
	 *
	 * @param ev ActionEvent object
	 */
	public void actionPerformed(ActionEvent ev)
	{
		if (ev.getSource() == resetButton)
			resetButtonClicked();
		else if (ev.getSource() == saveButton)
			saveButtonClicked();
	}

	private void resetButtonClicked()
	{
		carComponents.clearTextFields();
	}

	private void saveButtonClicked() {
        try {
            String manufacturer = carComponents.getManufacturerText().trim();
            String model = carComponents.getModelText().trim();
            String info = carComponents.getInfoText().trim();
            double kilometers = Double.parseDouble(carComponents.getKmText().trim());
            int price = Integer.parseInt(carComponents.getPriceText().trim());
            int year = Integer.parseInt(carComponents.getYearText().trim());

            if (!isValidManufacturer(manufacturer) || !isValidModel(model) ||
                !isValidYear(year) || !isValidKilometers(carComponents.getKmText().trim())) {
                return;
            }

            Car myCar = new Car(manufacturer, model, info);
            myCar.setKilometers(kilometers);
            myCar.setPrice(price);
            myCar.setYear(year);

            int result = carSystem.addNewCar(myCar);
            handleAddCarResult(result);
        } catch (NumberFormatException exp) {
            JOptionPane.showMessageDialog(carSystem, "An unknown error has occurred. Please ensure your fields meet the following requirements:\n" +
                    "The \"Year\" field must contain four numeric digits only\nThe \"Price\" field must contain a valid integer with no decimal places\nThe \"Km Traveled\" field must contain a number which can have a maximum of one decimal place", INVALID_FIELD_MESSAGE, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddCarResult(int result) {
        switch (result) {
            case CarsCollection.NO_ERROR:
                JOptionPane.showMessageDialog(carSystem, "Record added.", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                resetButtonClicked();
                carComponents.setFocusManufacturerTextField();
                break;
            case CarsCollection.CARS_MAXIMUM_REACHED:
                JOptionPane.showMessageDialog(carSystem, "The maximum amount of cars for that manufacturer has been reached.\nUnfortunately you cannot add any further cars to this manufacturer", "Problem adding car", JOptionPane.WARNING_MESSAGE);
                break;
            case CarsCollection.MANUFACTURERS_MAXIMUM_REACHED:
                JOptionPane.showMessageDialog(carSystem, "The maximum amount of manufacturers in the car system has been reached.\nUnfortunately you cannot add any further manufacturers to this system", "Problem adding car", JOptionPane.WARNING_MESSAGE);
                break;
            default:
                // Handle unexpected error codes
                JOptionPane.showMessageDialog(carSystem, "An unknown error has occurred.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidManufacturer(String manufacturer) {
        if (!validateString(manufacturer)) {
            JOptionPane.showMessageDialog(carSystem, "An error has occurred due to incorrect \"Manufacturer\" text field data.\nThis text field must contain any string of at least two non-spaced characters.", INVALID_FIELD_MESSAGE, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean isValidModel(String model) {
        if (!validateString(model)) {
            JOptionPane.showMessageDialog(carSystem, "An error has occurred due to incorrect \"Model\" text field data.\nThis text field must contain any string of at least two non-spaced characters.", INVALID_FIELD_MESSAGE, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean isValidYear(int year) {
        if (year < 1000 || year > 9999) {
            JOptionPane.showMessageDialog(carSystem, "An error has occurred due to incorrect \"Year\" text field data.\nThis text field must be in the form, YYYY. ie, 2007.", INVALID_FIELD_MESSAGE, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean isValidKilometers(String kmText) {
        if (!validateKilometers(kmText)) {
            JOptionPane.showMessageDialog(carSystem, "An error has occurred due to incorrect \"Km Traveled\" text field data.\nThis text field must contain a number with one decimal place only.", INVALID_FIELD_MESSAGE, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

	/**
	 * checks the argument. It is valid if there is more than 2 non-spaced characters.
	 *
	 * @param arg string to test
	 * @return true if valid, false otherwise
	 */
	private boolean validateString(String arg)
	{
		boolean valid = false;
		String[] splitted = arg.split(" "); // splits argument around spaces and creates an array

		for (int i = 0; i < splitted.length; i++)
		{
			// checks if the number of characters between a space is greater than 2
			valid = (splitted[i].length() > 2);
			if (valid)
				break;
		}

		return valid;
	}

	/**
	 * checks the argument It is valid if it contains a decimal value, with only one decimal place
	 *
	 * @param distance a double value expressed in a string
	 * @return true if valid, false otherwise
	 */
	private boolean validateKilometers(String distance)
	{
		boolean valid = false;
		String rem;
		StringTokenizer tokens = new StringTokenizer(distance, "."); // look for decimal point

		tokens.nextToken();

		if (tokens.hasMoreTokens()) // if true, there is a decimal point present
		{
			// get string representation of all numbers after the decimal point
			rem = tokens.nextToken();
			// if there's only one number after the decimal point, then it's valid
			if (rem.length() == 1)
				valid = true;
			else
			{
				// check if the user has typed something like 3.00, or even 3.00000
				if ((Integer.parseInt(rem)) % (Math.pow(10, rem.length() - 1)) == 0)
					valid = true;
				else
					valid=false;
			}
		}
		else // doesn't have a decimal place
			valid = true;

		return valid;
	}
}