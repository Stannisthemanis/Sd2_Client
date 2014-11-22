package meeto.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class Client {
	
	public static Scanner			SC	= new Scanner(System.in);
	public static String			USERNAME, PASSWORD;
	public static Socket			SOCKET;
	public static int				SERVERSOCKET;
	public static String			HOSTNAME[];
	public static DataInputStream	IN;
	public static DataOutputStream	OUT;
	
	public static void main(String[] args) {
		System.out.println();
		init();
		connect(0);
		
	}
	
	public static void init() {
		USERNAME = null;
		PASSWORD = null;
		SOCKET = null;
		HOSTNAME = new String[] { "localhost", "Roxkax", "ricardo" };
		SERVERSOCKET = 6000;
	}
	
	public static void connect(int i) {
		try {
			if (SOCKET != null)
				try {
					SOCKET.close();
				} catch (IOException e) {
				}
			SOCKET = new Socket(HOSTNAME[i % 3], SERVERSOCKET);
			IN = new DataInputStream(SOCKET.getInputStream());
			OUT = new DataOutputStream(SOCKET.getOutputStream());
			if (USERNAME == null)
				homeMenu();
			else
				loginMenu();
			
		} catch (IOException e) {
			connect((i + 1) % 3);
		}
	}
	
	// ------------------------------ MENUS -----------------------------
	
	public static void homeMenu() {
		int option = -1;
		String optionString;
		System.out.println("\n\n\n");
		do {
			System.out.println("1-> Login");
			System.out.println("2-> Register");
			System.out.println("0-> Leave");
			System.out.println("choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || optionString.length() == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			} else {
				option = Integer.parseInt(optionString);
				if ((option != 0 && option != 1 && option != 2)) {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
			}
		} while (!isNumeric(optionString) || (option != 0 && option != 1 && option != 2));
		if (option == 0) {
			System.exit(0);
		} else if (option == 1) {
			loginMenu();
		} else if (option == 2) {
			registerMenu();
		}
		
	}
	
	public static void loginMenu() {
		String username, password, tryAgain;
		if (USERNAME == null) {
			System.out.println("User name: ");
			username = SC.nextLine();
			System.out.println("PassWord: ");
			password = SC.nextLine();
			try {
				OUT.writeUTF(username + "," + password);
				while (!IN.readBoolean()) {
					System.out.println("\nUser invalid or allready logged in, try again? (y/n)");
					tryAgain = SC.nextLine();
					if (tryAgain.equalsIgnoreCase("n"))
						homeMenu();
					else if (tryAgain.equalsIgnoreCase("y")) {
						System.out.println("\n\n\nUser name: ");
						username = SC.nextLine();
						System.out.println("PassWord: ");
						password = SC.nextLine();
					}
					OUT.writeUTF(username + "," + password);
				}
			} catch (IOException e) {
				connect(0);
			}
			USERNAME = username;
			PASSWORD = password;
			mainMenu();
		} else {
			try {
				OUT.writeUTF(USERNAME + "," + PASSWORD);
			} catch (IOException e) {
				connect(0);
			}
		}
	}
	
	public static void registerMenu() {
		String userName, passWord, dob, finalInfo = "";
		System.out.println("Register new USER\n");
		System.out.println("Insert USER name:");
		userName = SC.nextLine();
		try {
			OUT.writeUTF(userName);
			while (IN.readBoolean()) {
				System.out.println("Name already exists, try again\n");
				System.out.println("Insert USER name:");
				userName = SC.nextLine();
				OUT.writeUTF(userName);
			}
		} catch (IOException e) {
			connect(0);
		}
		System.out.println("PassWord: ");
		passWord = SC.nextLine();
		System.out.println("Date of birthday (dd/mm/yyyy): ");
		dob = SC.nextLine();
		while (!testDateOfBirthDay(dob)) {
			System.out.println("Wrong format, try again\n");
			System.out.println("Date of birthday (dd/mm/yyyy): ");
			dob = SC.nextLine();
			
		}
		finalInfo = userName + "," + passWord + "," + dob;
		boolean success = false;
		try {
			OUT.writeUTF(finalInfo);
			success = IN.readBoolean();
		} catch (IOException e) {
			connect(0);
		}
		if (success) {
			USERNAME = userName;
			PASSWORD = passWord;
			mainMenu();
			System.out.println("Inserted wit success! ");
		} else {
			System.out.println("Not inserted with success...");
		}
		
	}
	
	public static void mainMenu() {
		int option;
		String optionString;
		System.out.println("\n\n\n\n\n\n\n");
		do {
			do {
				System.out.println("He " + USERNAME);
				System.out.println("Main Menu");
				System.out.println("1-> Meetings");
				System.out.println("2-> Messages (" + requestNumberOfMessegesToRead() + " new messages)");
				System.out.println("3-> TODO list (" + requestSizeToDo() + " actions to be done)");
				System.out.println("0-> Leave");
				System.out.print("Choose option: ");
				optionString = SC.nextLine();
				if (!isNumeric(optionString) || optionString.length() == 0) {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
			} while (!isNumeric(optionString) || optionString.length() == 0);
			option = Integer.parseInt(optionString);
			switch (option) {
				case 0:
					System.exit(0);
				case 1: {
					subMenuMeetings();
				}
					break;
				case 2: {
					subMenuMessages();
				}
					break;
				case 3: {
					System.out.println();
					subMenuTodo();
				}
					break;
				default: {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
					break;
			}
			
		} while (true);
	}
	
	private static void subMenuTodo() {
		System.out.println("\n\n\n\n\n\n\n\n");
		String options = requestActionItemsFromUser();
		ArrayList<Integer> rightOption = getRightOptions(options, 1);
		String optionString;
		int id_action_item;
		if (rightOption == null) {
			System.out.println("You have no action to be done");
			System.out.println("Insert any key to return");
			SC.nextLine();
			return;
		}
		rightOption.add(0);
		do {
			System.out.println(options);
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString)) || optionString.length() == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString) || (!rightOption.contains(Integer.parseInt(optionString))));
		id_action_item = Integer.parseInt(optionString);
		boolean sucess = false;
		if (id_action_item == 0) {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
			return;
		}
		do {
			System.out.println("1-> Mark as done");
			System.out.println("0-> Back");
			System.out.println("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || (Integer.parseInt(optionString) != 1 && Integer.parseInt(optionString) != 0)) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString) || (Integer.parseInt(optionString) != 1 && Integer.parseInt(optionString) != 0));
		if (Integer.parseInt(optionString) == 0) {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
			return;
		} else {
			do {
				System.out.println("Mark as done? (y/n)");
				optionString = SC.nextLine();
			} while (!optionString.equalsIgnoreCase("y") && !optionString.equalsIgnoreCase("n"));
			
			if (optionString.equals("y")) {
				sucess = requestMarkActionAsDone(id_action_item, true);
			} else if (optionString.equals("n")) {
				sucess = requestMarkActionAsDone(id_action_item, false);
			}
			if (sucess) {
				System.out.println("Invite accept with success!\n");
				return;
			} else {
				System.out.println("Invite not accepted...\n");
				return;
			}
		}
	}
	
	private static void subMenuMessages() {
		System.out.println("\n\n\n\n\n\n\n\n\n\n");
		String options = requestMessages();
		ArrayList<Integer> rightOption = getRightOptions(options, 1);
		String optionString;
		int id_invite;
		if (rightOption == null) {
			System.out.println("You have no message");
			System.out.println("Insert any key to return");
			SC.nextLine();
			return;
		}
		rightOption.add(0);
		do {
			System.out.println(options); // display all messages
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString))) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString)));
		id_invite = Integer.parseInt(optionString);
		if (id_invite == 0) {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
			return;
		}
		System.out.println("Resume from message " + id_invite);
		System.out.println(requestResumeMesage(id_invite));
		System.out.println("Do you accept this invite? (y/n)");
		optionString = SC.nextLine();
		// reply
		if (optionString.equalsIgnoreCase("y")) {
			replyInvite(true);
			System.out.println("Invite accept with success!\n");
		} else if (optionString.equalsIgnoreCase("n")) {
			replyInvite(false);
			System.out.println("Invite not accepted...\n");
		}
		
	}
	
	private static void subMenuMeetings() {
		String optionString;
		int option;
		do {
			do {
				System.out.println("\n\n\n\n\n\n");
				System.out.println("Menu Meetings");
				System.out.println("1-> Create new meeting");
				System.out.println("2-> Check upcoming meetings");
				System.out.println("3-> Check current Meetings");
				System.out.println("4-> Check past meetings");
				System.out.println("0-> Back");
				System.out.print("Choose option: ");
				optionString = SC.nextLine();
				if (!isNumeric(optionString) || optionString.length() == 0) {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
			} while (!isNumeric(optionString) || optionString.length() == 0);
			option = Integer.parseInt(optionString);
			if (option == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			switch (option) {
				case 1: {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("\nCreate new meeting: ");
					createNewMeeting();
				}
					break;
				case 2: {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					SubMenuUpcomingMeetings();
				}
					break;
				case 3: {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					SubMenuCurrentMeetings();
				}
					break;
				case 4: {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					SubMenupPastMeetings();
				}
					break;
				default: {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
					break;
			}
		} while (true);
	}
	
	public static void SubMenuUpcomingMeetings() {
		System.out.println("\n\n\n\n\n\n\n\n\n\n");
		String options = requestMessages();
		ArrayList<Integer> rightOption = getRightOptions(options, 1);
		String optionString;
		int id_meeting;
		if (rightOption == null) {
			System.out.println("You have no Upcoming meetings");
			System.out.println("Insert any key to return");
			SC.nextLine();
			return;
		}
		rightOption.add(0);
		do {
			System.out.println(options);
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString))) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString)));
		id_meeting = Integer.parseInt(optionString);
		do {
			if (id_meeting == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			System.out.println("Resume from meeting " + id_meeting);
			System.out.println("\n" + requestResumeMeeting(id_meeting));
			System.out.println("Options for meeting " + id_meeting);
			System.out.println("1-> Consult Agenda Items");
			System.out.println("2-> Add items to agenda");
			System.out.println("3-> Modify items in agenda");
			System.out.println("4-> Delete items from agenda");
			System.out.println("5-> Invite new user to this meeting");
			System.out.println("0-> Back");
			System.out.println("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || optionString.length() == 0
					|| (Integer.parseInt(optionString) < 0 && Integer.parseInt(optionString) > 5)) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString) || optionString.length() == 0
				|| (Integer.parseInt(optionString) < 0 && Integer.parseInt(optionString) > 5));
		if (Integer.parseInt(optionString) == 0) {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
			
		}
		switch (Integer.parseInt(optionString)) {
			case 1:
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Consult Agenda Items: ");
				subMenuConsultAgendaItems(id_meeting);
				break;
			case 2:
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				addItemstoAgenda(id_meeting);
				break;
			case 3:
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Modify items IN agenda: ");
				subMenuModifyAgendaItem(id_meeting);
				break;
			case 4:
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Delete items from agenda: ");
				subMenuDeleteItemstFromAgenda(id_meeting);
				break;
			case 5:
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Inviting new user ");
				subMenuInviteNewUser(id_meeting);
				break;
			default:
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
				break;
		}
	}
	
	public static void SubMenuCurrentMeetings() {
		System.out.println("\n\n\n\n\n\n\n\n\n\n");
		String options = requestMessages();
		ArrayList<Integer> rightOption = getRightOptions(options, 1);
		String optionString;
		int id_meeting;
		if (rightOption == null) {
			System.out.println("You have no current meetings");
			System.out.println("Insert any key to return");
			SC.nextLine();
			return;
		}
		rightOption.add(0);
		do {
			System.out.println(options);
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString))) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString)));
		id_meeting = Integer.parseInt(optionString);
		do {
			if (id_meeting == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			do {
				System.out.println("Resume from meeting " + id_meeting);
				System.out.println("\n" + requestResumeMeeting(id_meeting) + "\n");
				System.out.println("\nOptions for meeting " + id_meeting);
				System.out.println("1-> Discuss Agenda Items");
				System.out.println("2-> Add new action Item");
				System.out.println("0-> Back");
				System.out.println("Choose an option: ");
				optionString = SC.nextLine();
				if (!isNumeric(optionString) || optionString.length() == 0
						|| (Integer.parseInt(optionString) < 0 && Integer.parseInt(optionString) > 2)) {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
			} while (!isNumeric(optionString) || optionString.length() == 0
					|| (Integer.parseInt(optionString) < 0 && Integer.parseInt(optionString) > 2));
			if (Integer.parseInt(optionString) == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			switch (Integer.parseInt(optionString)) {
				case 1: {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Consult Agenda Items: ");
					subMenuConsultAgendaItemsCM(id_meeting);
				}
					break;
				case 2: {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Add new Action Item: ");
					addNewActionItem(id_meeting);
				}
					break;
				default: {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
					break;
			}
		} while (true);
	}
	
	public static void SubMenupPastMeetings() {
		System.out.println("\n\n\n\n\n\n\n\n\n\n");
		String options = requestMessages();
		ArrayList<Integer> rightOption = getRightOptions(options, 1);
		String optionString;
		int id_meeting;
		if (rightOption == null) {
			System.out.println("You have no past meetings");
			System.out.println("Insert any key to return");
			SC.nextLine();
			return;
		}
		rightOption.add(0);
		do {
			System.out.println(options);
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString))) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString)));
		id_meeting = Integer.parseInt(optionString);
		do {
			if (id_meeting == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			do {
				System.out.println("Resume from meeting " + id_meeting);
				System.out.println("\n" + requestResumeMeeting(id_meeting) + "\n");
				System.out.println("\nOptions from meeting " + id_meeting);
				System.out.println("1-> Consult Agenda Items");
				System.out.println("2-> Consult Action Items");
				System.out.println("0-> Back");
				System.out.println("Choose an option: ");
				optionString = SC.nextLine();
				if (!isNumeric(optionString) || optionString.length() == 0
						|| (Integer.parseInt(optionString) < 0 && Integer.parseInt(optionString) > 2)) {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
			} while (!isNumeric(optionString) || optionString.length() == 0
					|| (Integer.parseInt(optionString) < 0 && Integer.parseInt(optionString) > 2));
			if (Integer.parseInt(optionString) == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			switch (Integer.parseInt(optionString)) {
				case 1:
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Consult Agenda Items: ");
					subMenuConsultAgendaItemsPM(id_meeting);
					break;
				case 2:
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Consult Action Items: ");
					System.out.println(subMenuConsultActionItems(id_meeting));
					System.out.println("Press any key to continue...");
					SC.nextLine();
					break;
				default:
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
					break;
			}
		} while (true);
	}
	
	private static char[] subMenuConsultActionItems(int id_meeting) {
		String options = requestActionItemsFromMeeting(id_meeting);
		System.out.println(options);
		System.out.println("Insert any key to return!");
		SC.nextLine();
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
		return null;
	}


	public static void subMenuConsultAgendaItemsPM(int id_meeting) {
		System.out.println("\n\n\n\n\n\n\n\n\n\n");
		String options = requestAgendaItemsFromMeeting(id_meeting);
		ArrayList<Integer> rightOption = getRightOptions(options, 1);
		String optionString;
		int id_agenda_item;
		if (rightOption == null) {
			System.out.println("You have no agenda itens in this meeting");
			System.out.println("Insert any key to return");
			SC.nextLine();
			return;
		}
		rightOption.add(0);
		do {
			System.out.println(options);
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString))) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString)));
		id_agenda_item = Integer.parseInt(optionString);
		System.out.println(requestMessagesFromAgendaItem(id_agenda_item));
		System.out.println("Press any key to continue...");
		SC.nextLine();
	}
	
	public static void subMenuConsultAgendaItemsCM(int id_meeting) {
		System.out.println("\n\n\n\n\n\n\n\n\n\n");
		String options = requestAgendaItemsFromMeeting(id_meeting);
		ArrayList<Integer> rightOption = getRightOptions(options, 1);
		String optionString;
		int id_agenda_item;
		if (rightOption == null) {
			System.out.println("You have no agenda itens in this meeting");
			System.out.println("Insert any key to return");
			SC.nextLine();
			return;
		}
		rightOption.add(0);
		do {
			System.out.println(options);
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString))) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString)));
		id_agenda_item = Integer.parseInt(optionString);
		do {
			if (id_agenda_item == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			do {
				System.out.println("Options for Agenda item " + id_agenda_item);
				System.out.println("1-> Open chat");
				System.out.println("2-> Add key decsions");
				System.out.println("0-> Back");
				System.out.println("Choose an option: ");
				optionString = SC.nextLine();
				if (!isNumeric(optionString) || optionString.length() == 0 || Integer.parseInt(optionString) < 0
						|| Integer.parseInt(optionString) > 2) {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
			} while (!isNumeric(optionString) || optionString.length() == 0 || Integer.parseInt(optionString) < 0
					|| Integer.parseInt(optionString) > 2);
			if (Integer.parseInt(optionString) == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			switch (Integer.parseInt(optionString)) {
				case 1:
					System.out.println("\n\n\n");
					System.out.println(requestMessagesFromAgendaItem(id_agenda_item));
					chat(id_agenda_item);
					requestLeaveChat();
					break;
				case 2:
					System.out.println("Add/modify key decision");
					addNewKeyDecisionToAgendaitem(id_agenda_item);
					break;
				default:
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
					
					break;
			}
		} while (true);
	}
	
	public static void subMenuInviteNewUser(int optMeeting) {
		String userName;
		do {
			System.out.println("User to intive: ");
			userName = SC.nextLine();
			if (!testIfUserNamesExists(userName) || userName.length() == 0) {
				System.out.println("\n Name does not exist, try again\n");
			}
		} while (!testIfUserNamesExists(userName) || userName.length() == 0);
		boolean success = requestInviteNewUser(optMeeting, userName);
		if (success)
			System.out.println("\n User invited with success! ");
		else
			System.out.println("\n User is already invited...");
		System.out.println("Press any key to continue...");
		SC.nextLine();
	}
	
	public static void subMenuDeleteItemstFromAgenda(int id_meeting) {
		int id_agenda_item;
		String options = requestAgendaItemsFromMeeting(id_meeting), optionString;
		ArrayList<Integer> rightOption = getRightOptions(options, 0);
		if (rightOption == null) {
			System.out.println("You have no agenda itens in this meeting that can be deleted");
			System.out.println("Insert any key to return");
			SC.nextLine();
			return;
		}
		rightOption.add(0);
		String[] countOptions = options.split("\n");
		do {
			for (int i = 0; i < countOptions.length - 1; i++) {
				System.out.println(countOptions[i]);
			}
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString))) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
			
		} while (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString)));
		id_agenda_item = Integer.parseInt(optionString);
		if (id_agenda_item == 0) {
			return;
		}
		String deleteConfirm = "";
		do {
			System.out.println("Delete this item? (y/n)");
			deleteConfirm = SC.nextLine();
		} while (!deleteConfirm.equals("y") && !deleteConfirm.equals("n"));
		System.out.println("------------------");
		if (deleteConfirm.equalsIgnoreCase("y")) {
			boolean success = requestDeleteItemToAgenda(id_agenda_item);
			if (success) {
				System.out.println("Agenda item was deleted successfully!!");
			} else {
				System.out.println("Error deleting Item from Agenda....");
			}
			System.out.println("Insert any key to return ");
			SC.nextLine();
		}
	}
	
	public static void subMenuModifyAgendaItem(int id_meeting) {
		int id_agenda_item;
		String options = requestAgendaItemsFromMeeting(id_meeting), optionString;
		ArrayList<Integer> rightOption = getRightOptions(options, 0);
		if (rightOption == null) {
			System.out.println("You have no agenda itens in this meeting that can be modified");
			System.out.println("Insert any key to return");
			SC.nextLine();
			return;
		}
		rightOption.add(0);
		String[] countOptions = options.split("\n");
		do {
			for (int i = 0; i < countOptions.length - 1; i++) {
				System.out.println(countOptions[i]);
			}
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString))) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
			
		} while (!isNumeric(optionString) || !rightOption.contains(Integer.parseInt(optionString)));
		id_agenda_item = Integer.parseInt(optionString);
		if (id_agenda_item == 0)
			return;
		modifyNameFromAgendaItem(id_agenda_item);
	}
	
	public static void subMenuConsultAgendaItems(int id_meeting) {
		String options = requestAgendaItemsFromMeeting(id_meeting);
		System.out.println(options);
		System.out.println("Insert any key to return!");
		SC.nextLine();
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
	}
	
	private static String requestActionItemsFromMeeting(int id_meeting) {
		String result = "";
		try {
			OUT.write(18);
		} catch (Exception e) {
			connect(0);
			return requestActionItemsFromMeeting(id_meeting);
		}
		try {
			OUT.write(id_meeting);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestAgendaItemsFromMeeting(id_meeting);
		}
		return result;
	}
	
	public static boolean requestAddKeyDecisionToAgendaItem(int id_agenda_item, String newKeyDecision) {
		try {
			OUT.write(12);
		} catch (Exception e) {
			connect(0);
			return requestAddKeyDecisionToAgendaItem(id_agenda_item, newKeyDecision);
		}
		try {
			OUT.write(id_agenda_item);
			OUT.writeUTF(newKeyDecision);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestAddKeyDecisionToAgendaItem(id_agenda_item, newKeyDecision);
			
		}
	}
	
	public static void requestLeaveChat() {
		try {
			OUT.write(22);
		} catch (Exception e) {
			connect(0);
			requestLeaveChat();
		}
	}
	
	public static String requestMessagesFromAgendaItem(int id_agenda_item) {
		String result = "";
		try {
			OUT.write(23);
		} catch (Exception e) {
			connect(0);
			return requestMessagesFromAgendaItem(id_agenda_item);
		}
		try {
			OUT.write(id_agenda_item);
			result = IN.readUTF();
		} catch (IOException e) {
			return requestMessagesFromAgendaItem(id_agenda_item);
			
		}
		return result;
	}
	
	public static boolean requestInviteNewUser(int id_meeting, String username) {
		boolean success = false;
		try {
			OUT.write(24);
		} catch (Exception e) {
			connect(0);
			requestInviteNewUser(id_meeting, username);
		}
		try {
			OUT.write(id_meeting);
			OUT.writeUTF(username);
			success = IN.readBoolean();
			return success;
		} catch (IOException e) {
			connect(0);
			requestInviteNewUser(id_meeting, username);
			
		}
		return false;
	}
	
	public static boolean requestDeleteItemToAgenda(int id_agenda_item) {
		try {
			OUT.write(10);
		} catch (Exception e) {
			connect(0);
			return requestDeleteItemToAgenda(id_agenda_item);
		}
		try {
			OUT.write(id_agenda_item);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestDeleteItemToAgenda(id_agenda_item);
		}
	}
	
	public static boolean requestAddItemToAgenda(int id_meeting, String itemToadd) {
		try {
			OUT.write(9);
		} catch (Exception e) {
			connect(0);
			return requestAddItemToAgenda(id_meeting, itemToadd);
		}
		try {
			OUT.write(id_meeting);
			OUT.writeUTF(itemToadd);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestAddItemToAgenda(id_meeting, itemToadd);
			
		}
	}
	
	private static String requestAgendaItemsFromMeeting(int id_meeting) {
		String result = "";
		try {
			OUT.write(5);
		} catch (Exception e) {
			connect(0);
			return requestAgendaItemsFromMeeting(id_meeting);
		}
		try {
			OUT.write(id_meeting);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestAgendaItemsFromMeeting(id_meeting);
		}
		return result;
	}
	
	public static String requestResumeMeeting(int id_meeting) {
		String result = "";
		try {
			OUT.write(4);
		} catch (Exception e) {
			connect(0);
			return requestResumeMeeting(id_meeting);
		}
		try {
			OUT.write(id_meeting);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestResumeMeeting(id_meeting);
		}
		return result;
	}
	
	private static boolean requestMarkActionAsDone(int id_action_item, boolean decision) {
		try {
			OUT.write(16);
			OUT.write(id_action_item);
			OUT.writeBoolean(decision);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestMarkActionAsDone(id_action_item, decision);
		}
	}
	
	private static String requestActionItemsFromUser() {
		String result = "";
		try {
			OUT.write(15);
		} catch (Exception e) {
			connect(0);
			return requestActionItemsFromUser();
		}
		try {
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestActionItemsFromUser();
			
		}
		return result;
	}
	
	private static String requestResumeMesage(int id_invite) {
		String result = "";
		try {
			OUT.write(7);
		} catch (Exception e) {
			connect(0);
			return requestResumeMesage(id_invite);
		}
		try {
			OUT.write(id_invite);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestResumeMesage(id_invite);
			
		}
		return result;
	}
	
	public static boolean requestIfClientExists(String userName) {
		try {
			OUT.write(25);
		} catch (Exception e) {
			connect(0);
			return requestIfClientExists(userName);
		}
		try {
			OUT.writeUTF(userName);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestIfClientExists(userName);
			
		}
	}
	
	private static String requestMessages() {
		String result = "";
		try {
			OUT.write(6);
			result = IN.readUTF();
		} catch (Exception e) {
			connect(0);
			return requestMessages();
			
		}
		return result;
	}
	
	private static int requestSizeToDo() {
		try {
			OUT.write(14);
			return IN.read();
		} catch (IOException e) {
			connect(0);
			return requestSizeToDo();
		}
	}
	
	private static int requestNumberOfMessegesToRead() {
		try {
			OUT.write(8);
			return IN.read();
		} catch (IOException e) {
			connect(0);
			return requestNumberOfMessegesToRead();
		}
	}
	
	public static boolean requestServerNewMeeting(String request) {
		try {
			OUT.write(1);
		} catch (Exception e) {
			connect(0);
			return requestServerNewMeeting(request);
		}
		try {
			OUT.writeUTF(request);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestServerNewMeeting(request);
		}
	}
	
	public static boolean requestMofifyItemToAgenda(int id_agendaItem, String newAgendaItem) {
		try {
			OUT.write(11);
		} catch (Exception e) {
			connect(0);
			return requestMofifyItemToAgenda(id_agendaItem, newAgendaItem);
		}
		try {
			OUT.write(id_agendaItem);
			OUT.writeUTF(newAgendaItem);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestMofifyItemToAgenda(id_agendaItem, newAgendaItem);
			
		}
	}
	
	public static boolean requestAddNewAcionItem(int id_meeting, String newActionItem, String user) {
		try {
			OUT.write(13);
		} catch (Exception e) {
			connect(0);
			return requestAddNewAcionItem(id_meeting, newActionItem, user);
		}
		try {
			OUT.write(id_meeting);
			OUT.writeUTF(newActionItem);
			OUT.writeUTF(user);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestAddNewAcionItem(id_meeting, newActionItem, user);
			
		}
	}
	
	public static void addNewActionItem(int id_meeting) {
		String newActionItem = "", responsableUser = "";
		System.out.println("New ation Item: ");
		newActionItem = SC.nextLine();
		do {
			System.out.println("Responsable user: ");
			responsableUser = SC.nextLine();
			if (!testIfUserNamesExists(responsableUser) || responsableUser.length() == 0) {
				System.out.println("\n Name does not exist, try again\n");
			}
		} while (!testIfUserNamesExists(responsableUser) || responsableUser.length() == 0);
		boolean success = requestAddNewAcionItem(id_meeting, newActionItem, responsableUser);
		if (success)
			System.out.println("Agenda item was added successfully!!");
		else
			System.out.println("Error adding Item to Agenda....");
		
	}
	
	public static void modifyNameFromAgendaItem(int id_agenda_item) {
		String NewItemToDiscuss;
		System.out.println("New item to discuss: ");
		NewItemToDiscuss = SC.nextLine();
		boolean success = requestMofifyItemToAgenda(id_agenda_item, NewItemToDiscuss);
		if (success)
			System.out.println("Agenda item was modified successfully!!");
		else
			System.out.println("Error changing Item fom Agenda....");
	}
	
	private static void addItemstoAgenda(int id_meeting) {
		String itemToDiscuss;
		System.out.println("Add items to agenda: ");
		System.out.println("Item to discuss: ");
		itemToDiscuss = SC.nextLine();
		boolean success = requestAddItemToAgenda(id_meeting, itemToDiscuss);
		if (success)
			System.out.println("Agenda item was added successfully!!");
		else
			System.out.println("Error adding Item to Agenda....");
	}
	
	private static void createNewMeeting() {
		String responsible, desireOutCome, local, title, date = "", guests = null, agendaItems, request, dur;
		int duration;
		responsible = USERNAME;
		System.out.print("Title: ");
		title = SC.nextLine();
		System.out.print("Desire outcome: ");
		desireOutCome = SC.nextLine();
		System.out.print("Local: ");
		local = SC.nextLine();
		
		boolean dateTest = false;
		boolean pastDate = false;
		do {
			System.out.print("Date (dd/mm/yy hh:mm): ");
			date = SC.nextLine();
			dateTest = myDateTest(date);
			pastDate = checkPastDate(date);
			if (!dateTest) {
				System.out.println("Wrong format (min 0h:30m / max 2 years), try again");
			} else if (!pastDate) {
				System.out.println("Can't creat a meeting in the past (min 0h:30m / max 2 years), try again");
			}
		} while (!dateTest || !pastDate);
		date = date.replaceAll(" ", ",");
		
		boolean userTest = false;
		
		do {
			System.out.print("Guests (g1,g2,...): ");
			guests = SC.nextLine();
			if (guests == null || guests.length() == 0) {
				guests = "none";
				break;
			}
			userTest = testIfUserNamesExists(guests);
			if (userTest == false) {
				System.out.println("One or more USER names do not exist, try again");
			}
		} while (!userTest);
		
		System.out.print("agendaItems (ai1,ai2,...): ");
		agendaItems = SC.nextLine();
		do {
			System.out.print("Duration in minutes: ");
			dur = SC.nextLine();
			if (!isNumeric(dur) || dur.length() == 0) {
				System.out.println("\nBad format, try again: ");
			}
		} while (!isNumeric(dur));
		duration = Integer.parseInt(dur);
		System.out.println();
		request = responsible + "-" + desireOutCome + "-" + local + "-" + title + "-" + date + "-" + guests + "-" + agendaItems + "-"
				+ duration;
		boolean success = requestServerNewMeeting(request);
		if (success)
			System.out.println("Meeting successfully created!");
		else
			System.out.println("Error creating meeting...");
	}
	
	private static boolean replyInvite(boolean reply) {
		try {
			OUT.writeBoolean(reply);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return replyInvite(reply);
		}
		
	}
	
	public static boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean testDateOfBirthDay(String dob) {
		String localDate = dob;
		localDate = localDate.replaceAll("/", ",");
		String[] data = localDate.split(",");
		if (data.length != 3)
			return false;
		int day = Integer.parseInt(data[0]);
		int month = Integer.parseInt(data[1]);
		int year = Integer.parseInt(data[2]);
		
		Date actualDate = new Date(System.currentTimeMillis());
		int yearAux = actualDate.getYear() + 1902;
		
		if (year < (yearAux - 100) || year > yearAux) {
			return false;
		} else {
			if (month < 1 || month > 12) {
				return false;
			} else {
				if (day < 1 || day > 31) {
					return false;
				} else if (day > 28 && month == 2 && isLeapYear(year) == false) {
					return false;
				} else if (day > 29 && month == 2 && isLeapYear(year) == true) {
					return false;
				} else if (day > 30 && (month == 4 || month == 6 || month == 9 || month == 11)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean isLeapYear(int ano) {
		return (ano % 4 == 0);
	}
	
	@SuppressWarnings("deprecation")
	public static boolean myDateTest(String date) {
		String localDate = date;
		localDate = localDate.replaceAll("/", ",");
		localDate = localDate.replaceAll(":", ",");
		localDate = localDate.replaceAll(" ", ",");
		String[] data = localDate.split(",");
		if (data.length != 5)
			return false;
		int day = Integer.parseInt(data[0]);
		int month = Integer.parseInt(data[1]);
		int year = Integer.parseInt(data[2]);
		int hours = Integer.parseInt(data[3]);
		int minuts = Integer.parseInt(data[4]);
		
		Date actualDate = new Date(System.currentTimeMillis());
		int yearAux = actualDate.getYear() + 1902;
		if (hours < 0 || hours > 24)
			return false;
		else if (minuts < 0 || minuts > 59)
			return false;
		if (year < (yearAux - 100) || year > yearAux) {
			return false;
		} else {
			if (month < 1 || month > 12) {
				return false;
			} else {
				if (day < 1 || day > 31) {
					return false;
				} else if (day > 28 && month == 2 && isLeapYear(year) == false) {
					return false;
				} else if (day > 29 && month == 2 && isLeapYear(year) == true) {
					return false;
				} else if (day > 30 && (month == 4 || month == 6 || month == 9 || month == 11)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean checkPastDate(String date) {
		String localDate = date;
		localDate = localDate.replaceAll("/", ",");
		localDate = localDate.replaceAll(":", ",");
		localDate = localDate.replaceAll(" ", ",");
		String[] data = localDate.split(",");
		if (data.length != 5)
			return false;
		Calendar datetoTest = Calendar.getInstance();
		int day = Integer.parseInt(data[0]);
		int month = Integer.parseInt(data[1]);
		int year = Integer.parseInt(data[2]);
		int hours = Integer.parseInt(data[3]);
		int minuts = Integer.parseInt(data[4]);
		datetoTest.set(year, month, day, hours, minuts);
		
		Calendar now = Calendar.getInstance();
		now.setTime(new Date(System.currentTimeMillis()));
		now.add(Calendar.MONTH, 1);
		
		if (datetoTest.before(now)) {
			return false;
		}
		return true;
	}
	
	public static boolean testIfUserNamesExists(String guests) {
		guests = guests.replaceAll(", ", ",");
		String[] listOfGuests = guests.split(",");
		for (String g : listOfGuests) {
			if (!requestIfClientExists(g)) {
				return false;
			}
		}
		// System.out.println("false");
		return true;
	}
	
	private static ArrayList<Integer> getRightOptions(String options, int flag) {
		String[] rightOptions = options.split("\n");
		if (rightOptions.length == 1) {
			return null;
		}
		ArrayList<Integer> rightOptionsInt = new ArrayList<>();
		for (int i = 0; i < rightOptions.length; i++) {
			if (!rightOptions[i].split("-")[2].equals("Any Other Business") && flag == 0)
				rightOptionsInt.add(Integer.parseInt(rightOptions[i].split("-")[1]));
		}
		return rightOptionsInt;
	}
	
	public static void chat(int id_agenda_item) {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader bfr = new BufferedReader(isr);
		ReadingThread rt = new ReadingThread();
		String textRecived = null;
		System.out.println("Type '.quit' to leave");
		System.out.print("\n>>: ");
		while (true) {
			if (textRecived == null) {
				try {
					textRecived = bfr.readLine();
				} catch (Exception e) {
				}
			}
			if (textRecived.equalsIgnoreCase(".quit")) {
				rt.kill();
				return;
			}
			
			try {
				OUT.write(20);
				OUT.write(id_agenda_item);
				OUT.writeUTF(textRecived);
				textRecived = null;
			} catch (IOException e) {
				connect(0);
				System.out.println(requestMessagesFromAgendaItem(id_agenda_item));
				rt = new ReadingThread();
			}
		}
	}
	
	public static void addNewKeyDecisionToAgendaitem(int id_agenda_item) {
		String newKeyDecision;
		System.out.println("New key Decision: ");
		newKeyDecision = SC.nextLine();
		boolean success = requestAddKeyDecisionToAgendaItem(id_agenda_item, newKeyDecision);
		if (success) {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
			System.out.println("Key decision added successfully!!");
		} else {
			System.out.println("Error ading key decision....");
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
		}
	}
	
}

class ReadingThread extends Thread {
	protected DataInputStream	din;
	boolean						isRunning;
	
	public ReadingThread() {
		this.din = Client.IN;
		isRunning = true;
		this.start();
	}
	
	@Override
	public void run() {
		try {
			while (isRunning) {
				System.out.println(din.readUTF());
				if (isRunning == true)
					System.out.print(">>: ");
			}
		} catch (IOException e) {
			isRunning = false;
		}
	}
	
	public void kill() {
		isRunning = false;
	}
}
