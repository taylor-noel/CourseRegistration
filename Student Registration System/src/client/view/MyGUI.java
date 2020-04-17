package client.view;
import java.util.Optional;
import java.util.ResourceBundle.Control;

//CLASS IMPORTS
import client.controller.Controller;
import server.controller.CourseLite;
import server.model.Course;

// JAVAFX IMPORTS

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

// HOW TO MAKE JAVAFX ACCESSIBLE
/*right-click on the project and bring up the project properties dialog. Select "Build Path" in the left pane, and select the "Libraries" tab. You will see a "JRE System Library" entry. Expand that entry, and you will see an "Access Rules" subentry:

Select the "Access Rules" entry and click "Edit". Click "Add".

Under "Resolution", choose "Accessible", and under "Rule Pattern", enter javafx/** */


/**
 * Right now there is a bug when searching for courses, after you have 
 * searched for one course and the information is displayed, is you try searching for another 
 * it just writes the label on top of the first one rather than replacing it.
 * Main things that need to be added are:
 * 1. Sending the student information to the server and checking whether or not it is valid
 * 2. once you confirm they are a student, reading their information about their schedule and name
 * 3. adding/removing new courses to their schedule once the button enroll/unenroll is clicked then updating the courseList to display
 * 4. adding information to the course catalogue
 * @author Taylor
 *
 */


public class MyGUI extends Application{
	
	
	Stage window;
	Scene login, studentScene, adminScene, studentMenu;
	int width = 700;
	int height = 300;
	VBox rightPanel;
	


	public static void main(String [] args) {
		launch(args);
	}
	
	private Controller control;
	
	@Override 
	public void start(Stage primaryStage) {
		
		
		System.out.println("Client Started");
		
		
		// Creates new reference to ClientApp (Controller)
		control = new Controller(this);
		
		
		System.out.println("Building stage");
		
		
		window = primaryStage;
		window.setTitle("Login");
		
		//Layout 1 - Main window where user chooses between 
		//			 Student and Admin
		login = new Scene(optionScene(), width, height);
		
		//Layout 2 - Main Login window for Students where they
		//			 are prompted to input their id and password
		studentScene = new Scene(studentLogin(), width, height);
		
		//Layout 3 - Admin's main window (INCOMPLETE)
		adminScene = new Scene(adminLogin(), width, height);
		
		//Layout 4 - Student's main window with course search,
		// 			 their schedule, and where they can enroll in courses
		//studentMenu = new Scene(studentMenu(), width, height);

		window.setScene(login);
		window.show();
	}
	
///////////////////////////GRID PANES ////////////////////////////////////////
	
	
	/**
	 * Sets up the general layout of each window by setting 
	 * the padding, vertical gap, and horizontal gap
	 * @return general layout 
	 */
	private VBox makeLayout() {
		VBox layout = new VBox();
		layout.setPadding(new Insets(10,10,10,10));	
		return layout;
	}
	/**
	 * Creates the first scene which has the options for the 
	 * user to pick the student route or the admin one
	 * @return layout for the window
	 */
	private VBox optionScene() {
		VBox layout = makeLayout();
		layout.setSpacing(10);
		layout.setAlignment(Pos.CENTER);
		
		//University of Calgary Title
		Label title = new Label("University of Calgary");
		
		//Student Button
		Button student = new Button();
		student.setText("Sign In");
		student.setOnAction(e -> window.setScene(studentScene));
		
		//Admin Button
		Button admin = new Button();
		admin.setText("Admin");
		admin.setOnAction(e -> window.setScene(adminScene));
		
		//HBox for the buttons so they are on the same line
		HBox buttons = new HBox();
		buttons.setSpacing(100);
		buttons.getChildren().addAll(student, admin);
		buttons.setAlignment(Pos.CENTER);
		
		//Adding all the components to the layout
		layout.getChildren().addAll(title, buttons);
		
		return layout;
	}
	
	/**
	 * Creates the first student scene which has the text fields to 
	 * prompt the user to enter their id and password
	 * @return layout for the window
	 */
	private VBox studentLogin() {
		VBox layout = makeLayout();
		layout.setSpacing(50);
		HBox topPanel = new HBox();
		VBox centrePanel = new VBox();
		centrePanel.setSpacing(10);
		centrePanel.setAlignment(Pos.CENTER);
		
		//Student Id Block
		HBox idBlock = new HBox();
		idBlock.setSpacing(10);
		idBlock.setAlignment(Pos.CENTER);
		Label idLabel = new Label("Student ID");
		TextField idText = new TextField();
		idBlock.getChildren().addAll(idLabel,idText);
		
		//Student Password Block
		HBox passwordBlock = new HBox();
		passwordBlock.setSpacing(10);
		passwordBlock.setAlignment(Pos.CENTER);
		Label passwordLabel = new Label("Password");
		TextField passwordText = new TextField();
		passwordBlock.getChildren().addAll(passwordLabel, passwordText);
		
		//Login Response Label
		Label loginResponse = new Label("");
			
		//Login Button
		Button loginButton = new Button();
		loginButton.setText("Login");
		loginButton.setOnAction(e -> {
			loginStudent(idText, passwordText, loginResponse);
			});
		
		//Go Back Button
		Button goBack1 = new Button();
		goBack1.setText("Go Back");
		goBack1.setOnAction(e -> window.setScene(login));
		topPanel.getChildren().add(goBack1);
		
		centrePanel.getChildren().addAll(idBlock, passwordBlock, loginButton, loginResponse);
		
		//Adding all the components to the layout
		layout.getChildren().addAll(topPanel, centrePanel);
		
		return layout;
	}
	
	/**
	 * Creates the first admin scene which has the textfields to 
	 * prompt the user to enter their username and password
	 * @return layout for the window
	 */
	private VBox studentMenu() {
		VBox layout = makeLayout();
		layout.setSpacing(15);
		HBox title = new HBox();
		title.setSpacing(220);
		HBox panels = new HBox();
		
		panels.setSpacing(200);
		VBox leftPanel = new VBox();
		leftPanel.setSpacing(10);
		rightPanel = new VBox();
		rightPanel.setSpacing(10);
		panels.getChildren().addAll(leftPanel, rightPanel);
		
		//Schedule List
		ListView<String> courseList = new ListView<>();
		fillCourses(courseList);
				
		//Welcome label
		Label welcome = new Label("Welcome"); // Later, add name of student here
				
		//Log Out Button
		Button logoutButton = new Button();
		logoutButton.setText("Log Out");
		logoutButton.setOnAction(e -> window.setScene(login));
		
		//Browse Catalogue Button
		Button browse = new Button();
		browse.setText("Browse Catalogue");
		browse.setOnAction(e -> browseCatalogue());
		title.getChildren().addAll(welcome, browse, logoutButton);
		title.setAlignment(Pos.CENTER);
				
		//View Courses search
		Label courseLabel = new Label("View Course"); // later, add name of student here
		
		HBox searchPanel = new HBox();
		searchPanel.setSpacing(10);
		TextField searchTag = new TextField("search for a course");
		Button search = new Button();
		search.setText("Search");
		search.setOnAction(e -> courseDisplay(searchTag, leftPanel));
		searchPanel.getChildren().addAll(searchTag, search);
		leftPanel.getChildren().addAll(courseLabel, searchPanel);
				
		//Schedule Display
		Label schedule = new Label("Your Schedule");
		rightPanel.getChildren().addAll(schedule, courseList);
		
		//Adding all the components to the layout
		layout.getChildren().addAll(title, panels);
		
		return layout;
		
	}
	
	
	/**
	 * Creates the main window for the users which displays their schedule and
	 * prompts the user to search for and enroll in courses
	 * @return layout for the window
	 */
	private VBox adminLogin() {
		VBox layout = makeLayout();
		layout.setSpacing(50);
		HBox topPanel = new HBox();
		VBox centrePanel = new VBox();
		centrePanel.setSpacing(10);
		centrePanel.setAlignment(Pos.CENTER);
		
		//Student Id Block
		HBox idBlock = new HBox();
		idBlock.setSpacing(10);
		idBlock.setAlignment(Pos.CENTER);
		Label idLabel = new Label("Username");
		TextField idText = new TextField();
		idBlock.getChildren().addAll(idLabel,idText);
		
		//Student Password Block
		HBox passwordBlock = new HBox();
		passwordBlock.setSpacing(10);
		passwordBlock.setAlignment(Pos.CENTER);
		Label passwordLabel = new Label("Password");
		TextField passwordText = new TextField();
		passwordBlock.getChildren().addAll(passwordLabel, passwordText);
		
		//Login Response Label
		Label loginResponse = new Label("");
			
		//Login Button
		Button loginButton = new Button();
		loginButton.setText("Login");
		loginButton.setOnAction(e -> {
			loginStudent(idText, passwordText, loginResponse);
			});
		
		//Go Back Button
		Button goBack1 = new Button();
		goBack1.setText("Go Back");
		goBack1.setOnAction(e -> window.setScene(login));
		topPanel.getChildren().add(goBack1);
		
		centrePanel.getChildren().addAll(idBlock, passwordBlock, loginButton, loginResponse);
		
		//Adding all the components to the layout
		layout.getChildren().addAll(topPanel, centrePanel);
		
		return layout;
	}
	
	
	
	
	
	/////////// END OF GRID PANES //////////////////////////////////
	
	
	
	
	
	/**
	 * Gets what the student has entered into log-in field, logs in student
	 * @param inputID The textfield to input ID
	 * @param inputPass The textfield to input Password
	 */
	private void loginStudent(TextField inputID, TextField inputPass, Label responseLabel) {
		try {
			int id = Integer.parseInt(inputID.getText());
			String password = inputPass.getText();
			inputID.clear();
			inputPass.clear();
			control.login(id, password);
			System.out.println("Passed control");
			
		}catch(NumberFormatException e) {
			responseLabel.setText("Invalid username entered! Please enter a student ID!");
		}catch(Exception e) {
			// Sets the response label to an appropriate message based on issue
			// Note: incomplete
			responseLabel.setText(e.getMessage());
		}
	}
	
	public void setStudentMenu() {
		window.setScene(new Scene (studentMenu(), width, height));
	}
	
	/**
	 * Splits course name from string
	 * @param courseName
	 * @return
	 */
	private String splitCName(String courseName) {
		return courseName.split(" ")[0];
	}
	
	/**
	 * Splits course number from string
	 * @param courseName
	 * @return course number
	 */
	private int splitCNumber(String courseName) {
		return Integer.parseInt(courseName.split(" ")[1]);
	}
	
	/**
	 * Shows the course display of a certain course
	 * @param courseName Name of the course
	 */
	private void courseDisplay(TextField courseName, VBox leftPanel) {
		
		String input = courseName.getText();
		control.selectCourse(splitCName(input), splitCNumber(input));
		
		HBox courseInfo = new HBox();
		courseInfo.setSpacing(10);
		
		//Lecture Drop-Down
		ChoiceBox<String> lectures = new ChoiceBox<>();
		for(int i = 0; i < control.getSelectedCourseOfferings();)
			lectures.getItems().add("Lecture " + (++i));
		
		lectures.setValue("Lecture 1"); // default value
		
		//Course Name Label
		Label course = new Label(control.getSelectedCourseName());
		
		courseInfo.getChildren().addAll(course, lectures);
		
		//Spots Available
		Label spots = new Label("Spots: " + control.getSelectedCourseSpots());
		
		//Course Pre-Requisites
		Label preReqs = new Label("Pre-Requisites: ");
		
		//Other Available
		Label other = new Label("Other: ");
		
		HBox buttons = new HBox();
		buttons.setSpacing(15);
		
		//Enroll/Unenroll 
		Button enroll = new Button();
		enroll.setText("Enroll/Unenroll");
		enroll.setOnAction(e -> changeCourseEnrollment());
		
		//Search new Course
		Button searchNew = new Button();
		searchNew.setText("Search New Course");
		searchNew.setOnAction(e -> window.setScene(new Scene(studentMenu(), width, height)));
		
		buttons.getChildren().addAll(enroll, searchNew);
		
		leftPanel.getChildren().addAll(courseInfo, spots, preReqs, buttons);
	}
	
	private Boolean checkUnenrollment() {
		CourseLite [] courses = control.getSchedule();
		if(courses != null) {
		for(int i = 0; i < courses.length; i++) {
			if(courses[i] == control.getSelectedCourse()) {
				control.unenroll();
				System.out.println("UNENROLLED");
				return true;
			}
		}}
		return false;
	}
	
	private void changeCourseEnrollment() {
		Boolean check = checkUnenrollment();
		if(check == false)
			control.enroll();
		window.setScene(new Scene (studentMenu(), width, height));
	}
	/**
	 * Gets the student's schedule from the controller and then fills a ListView in order
	 * to display the information
	 * @param courseList Component used to display the student's schedule
	 */
	private void fillCourses(ListView<String> courseList){
		CourseLite [] courses = control.getSchedule();
		if(courses != null) {
			for(int i = 0; i < courses.length; i++) {
				courseList.getItems().add(courses[i].getName() + " " + courses[i].getNumber());
			}
		}
	}

	
	/**
	 * Window to display the course catalogue
	 */
	public void browseCatalogue() {
		Stage catalogue = new Stage();
		
		catalogue.initModality((Modality.APPLICATION_MODAL));
		catalogue.setTitle("Course Catalogue");
		catalogue.setMinWidth(250);
		
		VBox layout = new VBox();
		
		ObservableList<CourseLite> courses = FXCollections.observableArrayList();
		if(control.getCatalogue() != null) {
			for(CourseLite c: control.getCatalogue()) {
				courses.add(c);
			}
		}
		TableView<CourseLite> table;
		
		TableColumn<CourseLite, String> nameCol = new TableColumn<>("Course");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		TableColumn<CourseLite, Integer> numberCol = new TableColumn<>("Number");
		numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
		
		TableColumn<CourseLite, Integer> offeringsCol = new TableColumn<>("Number of Offerings");
		offeringsCol.setCellValueFactory(new PropertyValueFactory<>("offeringCount"));
		
		table = new TableView<>();
		table.setItems(courses);
		table.getColumns().addAll(nameCol, numberCol, offeringsCol);
		
		layout.getChildren().addAll(table);
		
		Scene scene = new Scene(layout);
		catalogue.setScene(scene);
		catalogue.showAndWait();
		
		
	}


}
