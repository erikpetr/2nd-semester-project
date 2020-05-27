package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import controller.ControlException;
import controller.OrderController;
import database.DataAccessException;
import gui.PopUp.PopUpType;
import model.Order;
import model.OrderItem;
import model.OrderRevision;

import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.CardLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ScrollPaneConstants;

public class SingleOrderWindow extends JFrame
{
	// Order Controller
	private static OrderController orderController;

	// Order
	private static Order order;
	
	// Panels
	private JPanel panel;
	private JTable table_items;
	private JTable table_rev_items;
	
	
	/*
	 * Run the frame
	 */
	public static void run(int orderID) 
	{
		EventQueue.invokeLater(() -> {
			try {
				// Get Order Controller
				orderController = new OrderController();
	
				// Get order
				order = orderController.getOrder(orderID);
				
				// Build window
				new SingleOrderWindow();
			} catch (Exception e) {
				PopUp.newPopUp(null, "Couldn't start the application.\nThere has been a catastrophic error.\nThe program will terminate\n" +
						e.getMessage(), "Failure", PopUp.PopUpType.ERROR);
				System.exit(-1);
			}
		});
	}
	
	/*
	 * Create JFramme
	 */
	public SingleOrderWindow()
	{				
		// Frame size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(100, 100, (int) (screenSize.width * 0.5), (int) (screenSize.height * 0.7));
		 
		// Main panel
		panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setLayout(new BorderLayout());
		
		// --- HEADER ---
		JPanel header = new JPanel();
		FlowLayout flowLayout = (FlowLayout) header.getLayout();
		flowLayout.setAlignOnBaseline(true);
		flowLayout.setVgap(10);
		flowLayout.setHgap(10);
		
		// Set border
		//header.setBorder(BorderFactory.createLineBorder(Color.black));
		
		// title
		header.add(titlePanel());
		// Add header to main panel
		panel.add(header, BorderLayout.NORTH);
		// --- /HEADER ---
		
		// --- BODY ---
		// Scroll Pane
		JScrollPane scrollPane = new JScrollPane();
		//scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		// Body panel
		JPanel body = new JPanel();
		body.setLayout(new GridLayout(0,1));
		// Add body panel to scroll panel
		scrollPane.setViewportView(body);
		
		// Order details
		body.add(orderDetails());
		
		// Order items
		body.add(orderItems());
		
		// Revisions
		body.add(orderRevisions());
		// --- /BODY ---
		
		// Set operation close ofJFrame
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Set content pane
		setContentPane(panel);
		// Set visible
		setVisible(true);
	}
	
	/*
	 * Title panel for window
	 */
	private Component titlePanel() 
	{
		// Title label
		JLabel title = new JLabel("ORDER DESCRIPTION (ORDER ID : " + order.getId() + ")");
		return title;
	}

	/*
	 * Creates panel for main details
	 */
	private Component orderDetails() 
	{
		// Details panel
		JPanel details = new JPanel();
		FlowLayout fl_details = new FlowLayout(FlowLayout.CENTER, 50, 30);
		details.setLayout(fl_details);
		// labels
		JLabel date = new JLabel("Date: " + order.getDate());
		details.add(date);
		JLabel warehouse = new JLabel("Warehouse: " + order.getWarehouse());
		details.add(warehouse);
		JLabel store = new JLabel("Store: " + order.getStore());
		details.add(store);
		JLabel price = new JLabel("Total Price: " + order.calculateTotalPrice());
		details.add(price);
		
		return details;
	}
	
	/*
	 * Creates scroll panel for order items
	 */
	private Component orderItems()
	{
		JPanel items = new JPanel();

		// Title
		JLabel title = new JLabel("Items");
		//title_items.setHorizontalAlignment(SwingConstants.CENTER);
		items.add(title);
		
		if (order.getItems().size() > 0)
		{			
			// Column names
			String[] columnNames = {
					"Product",
					"Quantity",
					"Unit Price"
					};
			
			// Convert items list
			Object[][] alldata = new Object[order.getItems().size()][];
			int i = 0;
			for (OrderItem row : order.getItems())
			{
				Object[] data = {
						row.getProduct(),
						row.getQuantity(),
						row.getUnitPrice()
				};

				alldata[i] = data;
				i++;
			}

			// Items table
			JTable table = new JTable(alldata, columnNames) {
				@Override
				public boolean editCellAt(int row, int column, java.util.EventObject e) {
					return false;
				}
			};
			
			// Remove row selection
			table.setRowSelectionAllowed(false);
			
			// Add table to scroll pane
			items.add(table_items);
		}
		else
		{
			JLabel noresults = new JLabel("No items on this order.");
			items.add(noresults);
		}
		
		return items;
	}
	
	/*
	 * Creates panel for revisions
	 */
	private Component orderRevisions()
	{
		// Panel
		JPanel revisions = new JPanel();
		revisions.setLayout(new BoxLayout(revisions, BoxLayout.Y_AXIS));
		
		JLabel title = new JLabel("Revisions");
		revisions.add(title);
		
		for(OrderRevision revision : order.getRevisions())
		{
			JLabel single_revision = new JLabel(revision.toString());
			single_revision.add(revisions);
		}
		
		return revisions;
	}
	 
	/*
	 * Creates panel for revisions
	 */
	/*private Component revisions()
	{
		// Panel
		JPanel revisions = new JPanel();
		revisions.setLayout(new BoxLayout(revisions, BoxLayout.Y_AXIS));
		
		// Dropbox
		String[] revisions_status = new String[order.getRevisions().size()];
		List<OrderRevision> order_revisions = order.getRevisions();
		for (int i = 0; i < order.getRevisions().size(); i++)
		{
			revisions_status[i] = order_revisions.get(i).toString();
		}
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(revisions_status));
		revisions.add(comboBox);
		
		// Labels
		JPanel revision_details = new JPanel();
		revisions.add(revision_details);
		
		JLabel Date = new JLabel("Date");
		revision_details.add(Date);
		
		JLabel Status = new JLabel("Status");
		revision_details.add(Status);
		
		JLabel Note = new JLabel("Note");
		revision_details.add(Note);

		// Title
		JLabel title = new JLabel("Revision");
		//title_items.setHorizontalAlignment(SwingConstants.CENTER);
		revisions.add(title);
		
		if (order.getRevisions().getItemsChanged().size() > 0)
		{
			// Scroll panel for table
			JScrollPane scrollPane = new JScrollPane();
			items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
			
			// Add to panel
			items.add(scrollPane);
				
			// Column names
			String[] columnNames = {
					"Product",
					"Quantity",
					"Unit Price"
					};
			
			// Convert items list
			Object[][] alldata = new Object[order.getItems().size()][];
			int i = 0;
			for (OrderItem row : order.getItems())
			{
				Object[] data = {
						row.getProduct(),
						row.getQuantity(),
						row.getUnitPrice()
				};

				alldata[i] = data;
				i++;
			}

			// Items table
			JTable table = new JTable(alldata, columnNames) {
				@Override
				public boolean editCellAt(int row, int column, java.util.EventObject e) {
					return false;
				}
			};
			
			// Remove row selection
			table.setRowSelectionAllowed(false);
			
			// Add table to scroll pane
			scrollPane.add(table_items);
			
			return scrollPane;
		}
		else
		{
			JLabel noresults = new JLabel("No items on this order.");
			return noresults;
		}
	}*/
	
}
