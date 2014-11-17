package meeto.client;

import java.io.*;
import java.net.Socket;
import java.sql.Date;
import java.util.Calendar;
import java.util.Scanner;

public class Client {

	public static Scanner SC = new Scanner(System.in);
	public static String USERNAME, PASSWORD;
	public static Socket SOCKET;
	public static int SERVERSOCKET;
	public static String HOSTNAME[];
	public static DataInputStream IN;
	public static DataOutputStream OUT;

	public static void main(String[] args) {
		init();
		connect(0);

	}

	public static void init() {
		USERNAME = null;
		PASSWORD = null;
		SOCKET = null;
		HOSTNAME = new String[]{"localhost", "Roxkax", "ricardo"};
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
			loginMenu();
		} catch (IOException e) {
			connect((i + 1) % 3);
		}
	}

	// ------------------------------ MENUS -----------------------------

	public static void loginMenu() {
		int option;
		String name, password, tryagain = "y", optionString;
		boolean logIn = false;
		if (USERNAME == null) {
			do {
				do {
					System.out.println("\n\n\n");
					System.out.println("1-> Login");
					System.out.println("2-> Register");
					System.out.println("0-> Leave");
					System.out.println("choose an option: ");
					optionString = SC.nextLine();
					if (!isNumeric(optionString) || optionString.length() == 0) {
						System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
						System.out.println("Wrong option");
					}
				} while (!isNumeric(optionString) || optionString.length() == 0);
				option = Integer.parseInt(optionString);
				if (option == 0) {
					System.exit(0);
				}
				switch (option) {
					case 1 : {
						do {
							System.out.println("User name: ");
							name = SC.nextLine();
							System.out.println("PassWord: ");
							password = SC.nextLine();
							try {
								OUT.writeUTF(name + "," + password);
								logIn = IN.readBoolean();
							} catch (IOException e) {
								connect(0);
							}
							if (!logIn) {
								do {
									System.out
											.println("\nUser invalid or allready logged in, try again? (y/n)");
									tryagain = SC.nextLine();
									System.out.println();
								} while (!tryagain.equalsIgnoreCase("y")
										&& !tryagain.equalsIgnoreCase("n"));

								if (tryagain.equalsIgnoreCase("n")) {
									System.exit(0);
								}
							}
						} while (!logIn);
						if (tryagain.equalsIgnoreCase("y")) {
							USERNAME = name;
							PASSWORD = password;
							System.out.println("\n\nWelcome " + name);
							mainMenu();
						}

					}
						break;
					case 2 : {
						registerNewClient();
					}
						break;
					default : {
						System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
						System.out.println("Wrong option");
					}
						break;
				}
			} while (true);
		} else {
			try {
				OUT.writeUTF(USERNAME + "," + PASSWORD);
			} catch (IOException e) {
				connect(0);
			}
		}
	}

	public static void mainMenu() {
		int option;
		String optionString;
		System.out.println("\n\n");
		do {
			do {
				System.out.println("Main Menu");
				System.out.println("1-> Meetings");
				System.out.println("2-> Messages ("
						+ requestNumberOfMessegesToRead() + " new messages)");
				System.out.println("3-> TODO list (" + requestSizeToDo()
						+ " actions to be done)");
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
				case 0 :
					System.exit(0);
				case 1 : {
					subMenuMeetings();
				}
					break;
				case 2 : {
					subMenuMessages();
				}
					break;
				case 3 : {
					System.out.println();
					subMenuTodo();
				}
					break;
				default : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
					break;
			}

		} while (true);
	}

	public static void subMenuMessages() {
		int optUm, size;
		String dec = "", optionString;
		String options = requestMessages();
		String[] countOptions = options.split("\n");
		size = countOptions.length;
		System.out.println(size);
		if (size == 1 && countOptions[0].equals("")) {
			System.out.println("You have no message");
			System.out.println("Insert any key to return");
			SC.next();
			return;
		}
		do {
			System.out.println(options); // display all messages
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString)
					|| Integer.parseInt(optionString) < 0
					|| Integer.parseInt(optionString) > requestNumberOfMessegesToRead()) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString)
				|| Integer.parseInt(optionString) < 0
				|| Integer.parseInt(optionString) > requestNumberOfMessegesToRead());
		optUm = Integer.parseInt(optionString);
		do {
			if (optUm == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			System.out.println("Resume from message " + optUm);
			System.out.println(requestResumeMesage(optUm));
			System.out.println("Do you accept this invite? (y/n)");
			dec = SC.next();
			dec = dec.toLowerCase();
			// reply
			if (dec.equalsIgnoreCase("y")) {
				replyInvite(true);
				System.out.println("Invite accept with success!\n");
				break;
			} else if (dec.equalsIgnoreCase("n")) {
				replyInvite(false);
				System.out.println("Invite not accepted...\n");
				break;
			}
		} while (true);
	}

	public static void subMenuMeetings() {
		String optionString;
		int option;
		do {
			do {
				System.out.println("\n\n\n");
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
				case 1 : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("\nCreate new meeting: ");
					creatNewMeeting();
				}
					break;
				case 2 : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					SubMenuUpcomingMeetings();
				}
					break;
				case 3 : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					SubMenuCurrentMeetings();
				}
					break;
				case 4 : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					SubMenupPastMeetings();
				}
					break;
				default : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
					break;
			}
		} while (true);
	}

	public static void SubMenuUpcomingMeetings() {
		int size, optUm, optAi;
		System.out.println("All upcoming meetings: ");
		String options = requestUpcomingMeetings();
		String[] countOptions = options.split("\n");
		size = countOptions.length;
		if (size == 1 && countOptions[0].equals("")) {
			System.out.println("No meetings found");
			System.out.println("Insert any key to return");
			SC.next();
			return;
		}
		String optionString;
		do {
			System.out.println(options); // display name of all upcoming
											// meetings
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString)
					|| optionString.length() == 0
					|| (Integer.parseInt(optionString) < 0 || Integer
							.parseInt(optionString) > size)) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString)
				|| optionString.length() == 0
				|| (Integer.parseInt(optionString) < 0 || Integer
						.parseInt(optionString) > size));
		optUm = Integer.parseInt(optionString);

		do {
			if (optUm == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			System.out.println("Resume from meeting " + optUm);
			System.out.println("\n" + requestResumeUpcumingMeeting(optUm)); // resume
																			// of
																			// chosen
																			// meeting
			System.out.println("Options for meeting " + optUm);
			System.out.println("1-> Consult Agenda Items");
			System.out.println("2-> Add items to agenda");
			System.out.println("3-> Modify items in agenda");
			System.out.println("4-> Delete items from agenda");
			System.out.println("5-> Invite new user to this meeting");
			System.out.println("0-> Back");
			System.out.println("Choose an option: ");
			optionString = SC.nextLine();
			System.out.println("|" + optionString + "| legth= "
					+ optionString.length());
			if (!isNumeric(optionString) || optionString.length() == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong optionhhhh");
			}
			System.out.println("while");
		} while (!isNumeric(optionString) || optionString.length() == 0);
		optAi = Integer.parseInt(optionString);
		if (optAi == 0) {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");

		}
		switch (optAi) {
			case 1 :
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Consult Agenda Items: ");
				SubMenuConsultAgendaItemsUM(optUm);
				break;
			case 2 :
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				addItemstoAgenda(optUm);
				break;
			case 3 :
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Modify items IN agenda: ");
				subMenuModifyAgendaItem(optUm);
				break;
			case 4 :
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Delete items from agenda: ");
				subMenuDeleteItemstFromAgenda(optUm);
				break;
			case 5 :
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Inviting new user ");
				subMenuInviteNewUser(optUm);
				break;
			default :
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
				break;
		}
	}

	public static void SubMenuCurrentMeetings() {
		int size, optMeeting, optAi;
		System.out.println("All Current meetings: ");
		String options = requestCurrentMeetings();
		String[] countOptions = options.split("\n");
		size = countOptions.length;
		// System.out.println("size-> "+size);
		if (size == 1 && countOptions[0].equals("")) {
			System.out.println("No meetings found");
			System.out.println("Insert any key to return");
			SC.next();
			return;
		}
		String optionString;
		do {
			System.out.println(options); // display name of all upcoming
											// meetings
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();

			if (!isNumeric(optionString)
					|| optionString.length() == 0
					|| (Integer.parseInt(optionString) < 0 || Integer
							.parseInt(optionString) > size)) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString)
				|| optionString.length() == 0
				|| (Integer.parseInt(optionString) < 0 || Integer
						.parseInt(optionString) > size));
		optMeeting = Integer.parseInt(optionString);
		do {
			if (optMeeting == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			do {
				System.out.println("Resume from meeting " + optMeeting);
				System.out.println("\n"
						+ requestResumeCurrentMeetings(optMeeting) + "\n"); // resume
																			// of
																			// chosen
																			// meeting
				System.out.println("\nOptions for meeting " + optMeeting);
				System.out.println("1-> Discuss Agenda Items");
				System.out.println("2-> Add new action Item");
				System.out.println("0-> Back");
				System.out.println("Choose an option: ");
				optionString = SC.nextLine();

				if (!isNumeric(optionString) || optionString.length() == 0) {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
			} while (!isNumeric(optionString) || optionString.length() == 0);
			optAi = Integer.parseInt(optionString);
			if (optAi == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			switch (optAi) {
				case 1 : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Consult Agenda Items: ");
					SubMenuConsultAgendaItemsCM(optMeeting);
				}
					break;
				case 2 : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Add new Action Item: ");
					addNewActionItem(optMeeting);
				}
					break;
				default : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
					break;
			}
		} while (true);
	}

	public static void SubMenupPastMeetings() {
		int size, optUm, optAi;
		System.out.println("All past meetings: ");
		String options = requestPastMeetings();
		String[] countOptions = options.split("\n");
		size = countOptions.length;
		if (size == 1 && countOptions[0].equals("")) {
			System.out.println("No meetings found");
			System.out.println("Insert any key to return");
			SC.next();
			return;
		}
		String optionString;
		do {
			System.out.println(options); // display name of all upcoming
											// meetings
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString)
					|| optionString.length() == 0
					|| (Integer.parseInt(optionString) < 0 || Integer
							.parseInt(optionString) > size)) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString)
				|| optionString.length() == 0
				|| (Integer.parseInt(optionString) < 0 || Integer
						.parseInt(optionString) > size));
		optUm = Integer.parseInt(optionString);
		do {
			if (optUm == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			do {
				System.out.println("Resume from meeting " + optUm);
				System.out.println("\n" + requestResumePastMeeting(optUm)
						+ "\n"); // resume of chosen meeting
				System.out.println("\nOptions from meeting " + optUm);
				System.out.println("1-> Consult Agenda Items");
				System.out.println("2-> Consult Action Items");
				System.out.println("0-> Back");
				System.out.println("Choose an option: ");
				optionString = SC.nextLine();
				if (!isNumeric(optionString) || optionString.length() == 0) {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
			} while (!isNumeric(optionString) || optionString.length() == 0);
			optAi = Integer.parseInt(optionString);
			if (optAi == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			switch (optAi) {
				case 1 : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Consult Agenda Items: ");
					SubMenuConsultAgendaItemsPM(optUm);
				}
					break;
				case 2 : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Consult Action Items: ");
					System.out.println(requestActionItemsPastMeeting(optUm));
					System.out.println("Press any key to continue...");
					SC.nextLine();
				}
					break;
				default : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
					break;
			}
		} while (true);
	}

	public static void SubMenuConsultAgendaItemsUM(int opt) {
		String options = requestAgendaItemsFromUpComingMeeting(opt);
		do {
			System.out.println(options); // display name of all agenda items
			System.out.println("Insert any key to return!");
			// SC.next();
			SC.nextLine();
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
			break;
		} while (true);
	}

	public static void SubMenuConsultAgendaItemsPM(int opt) {
		int optUm, size, opt2;
		String options = requestAgendaItemsFromPastMeeting(opt);
		String[] countOptions = options.split("\n");
		size = countOptions.length;
		if (size == 1 && countOptions[0].equals("")) {
			System.out.println("No items found");
			System.out.println("Insert any key to return");
			SC.next();
			return;
		}
		String optionString;
		do {
			System.out.println(options); // display name of all upcoming
											// meetings
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString)
					|| optionString.equals("")
					|| (Integer.parseInt(optionString) < 0 || Integer
							.parseInt(optionString) > size)) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString)
				|| optionString.equals("")
				|| (Integer.parseInt(optionString) < 0 || Integer
						.parseInt(optionString) > size));
		optUm = Integer.parseInt(optionString);
		System.out.println(resquestChatFromItemPastMeeting(opt, optUm));
		System.out.println("Press any key to continue...");
		// SC.next();
		SC.nextLine();
	}

	public static void SubMenuConsultAgendaItemsCM(int optMeeting) {
		int optItem, opt2, size;
		String options = requestAgendaItemsFromCurrentMeetings(optMeeting);
		String[] countOptions = options.split("\n");
		size = countOptions.length;
		if (size == 1 && countOptions[0].equals("")) {
			System.out.println("No items found");
			System.out.println("Insert any key to return");
			SC.next();
			return;
		}
		String optionString;
		do {
			System.out.println(options); // display name of all upcoming
											// meetings
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString)
					|| optionString.length() == 0
					|| (Integer.parseInt(optionString) < 0 || Integer
							.parseInt(optionString) > size)) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString)
				|| optionString.length() == 0
				|| (Integer.parseInt(optionString) < 0 || Integer
						.parseInt(optionString) > size));
		optItem = Integer.parseInt(optionString);
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
		do {
			if (optItem == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			do {
				System.out.println("Options for Agenda item " + optItem);
				System.out.println("1-> Open chat");
				System.out.println("2-> Add key decsions");
				System.out.println("0-> Back");
				System.out.println("Choose an option: ");
				optionString = SC.nextLine();
				if (!isNumeric(optionString) || optionString.length() == 0) {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
			} while (!isNumeric(optionString) || optionString.length() == 0);
			opt2 = Integer.parseInt(optionString);
			if (opt2 == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			switch (opt2) {
				case 1 : {
					System.out.println("\n\n\n");
					System.out.println(requestMessagesFromAgendaItem(
							optMeeting, optItem));
					chat(optMeeting, optItem);
					requestLeaveChat(optMeeting, optItem);
				}
					break;
				case 2 : {
					System.out.println("Add/modify key decision");
					addNewKeyDecisionToAgendaitem(optMeeting, optItem);
				}
					break;
				default : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
					break;
			}
		} while (true);
	}

	public static void subMenuModifyAgendaItem(int optMeeting) {
		int optItemtoModify, size;
		String options = requestAgendaItemsFromUpComingMeeting(optMeeting), optString;
		String[] countOptions = options.split("\n");
		size = countOptions.length;
		options = options.replaceAll("Any other businness", "");
		do {
			for (int i = 0; i < size - 1; i++) {
				System.out.println(countOptions[i]);
			}
			System.out.println();
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optString = SC.nextLine();
			if (!isNumeric(optString)
					|| (Integer.parseInt(optString) < 0 || Integer
							.parseInt(optString) > (size - 1))
					|| optString.length() == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}

		} while (!isNumeric(optString)
				|| (Integer.parseInt(optString) < 0 || Integer
						.parseInt(optString) > (size - 1))
				|| optString.length() == 0);
		optItemtoModify = Integer.parseInt(optString);
		if (optItemtoModify == 0)
			return;
		modifyNameFromAgendaItem(optMeeting, optItemtoModify);
	}

	public static void subMenuTodo() {
		int size, optActionItem, optAux;
		System.out.println("All my actions to be done: ");
		String options = requestActionItemsFromUser(), optionString;
		String[] countOptions = options.split("\n");
		size = countOptions.length;
		if (size == 1 && countOptions[0].equals("")) {
			System.out.println("You have no message");
			System.out.println("Insert any key to return");
			SC.next();
			return;
		}
		do {
			System.out.println(options); // display name of all upcoming
											// meetings
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optionString = SC.nextLine();
			if (!isNumeric(optionString)
					|| (Integer.parseInt(optionString) < 0 || Integer
							.parseInt(optionString) > requestSizeToDo())
					|| optionString.length() == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}
		} while (!isNumeric(optionString)
				|| (Integer.parseInt(optionString) < 0 || Integer
						.parseInt(optionString) > requestSizeToDo())
				|| optionString.length() == 0);
		optActionItem = Integer.parseInt(optionString);
		do {
			boolean aux = false;
			if (optActionItem == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			do {
				System.out.println("1-> Mark as done");
				System.out.println("0-> Back");
				System.out.println("Choose an option: ");
				optionString = SC.nextLine();
				if (!isNumeric(optionString)
						|| (Integer.parseInt(optionString) < 0 || Integer
								.parseInt(optionString) > size)
						|| optionString.length() == 0) {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
			} while (!isNumeric(optionString)
					|| (Integer.parseInt(optionString) < 0 || Integer
							.parseInt(optionString) > size)
					|| optionString.length() == 0);
			optAux = Integer.parseInt(optionString);
			if (optAux == 0) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			switch (optAux) {
				case 1 : {
					String dec;

					do {
						System.out.println("Mark as done? (y/n)");
						dec = SC.next();
						dec = dec.toLowerCase();
						// reply
					} while (!dec.equals("y") && !dec.equals("n"));
					if (dec.equals("y")) {
						aux = requestMarkActionAsDone(optActionItem, true);
					} else if (dec.equals("n")) {
						aux = requestMarkActionAsDone(optActionItem, false);
					}

					// response
					if (aux) {
						System.out.println("Invite accept with success!\n");
						return;
					} else {
						System.out.println("Invite not accepted...\n");
						return;
					}
				}
				default : {
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
					System.out.println("Wrong option");
				}
					break;
			}
			if (aux)
				break;
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
		System.out.println(".....");
		boolean success = requestInviteNewUser(optMeeting, userName);
		if (success)
			System.out.println("\n User invited with success! ");
		else
			System.out.println("\n User is already invited...");
		System.out.println("Press any key to continue...");
		SC.next();
	}

	// -------------------------------------- REQUEST/REPLY

	public static int requestNumberOfMessegesToRead() {
		try {
			OUT.write(10);
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

	public static String requestUpcomingMeetings() {
		String result = "";
		try {
			OUT.write(2);
			result = IN.readUTF();
		} catch (Exception e) {
			connect(0);
			return requestUpcomingMeetings();
		}
		return result;
	}

	public static String requestPastMeetings() {
		String result = "";
		try {
			OUT.write(3);
			result = IN.readUTF();
		} catch (Exception e) {
			connect(0);
			return requestPastMeetings();
		}
		return result;
	}

	public static String requestMessages() {
		String result = "";
		try {
			OUT.write(8);
			result = IN.readUTF();
		} catch (Exception e) {
			connect(0);
			return requestMessages();

		}
		return result;
	}

	public static String requestAgendaItemsFromUpComingMeeting(int opt) {
		String result = "";
		try {
			OUT.write(6);
		} catch (Exception e) {
			connect(0);
			return requestAgendaItemsFromUpComingMeeting(opt);
		}
		try {
			OUT.write(opt);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestAgendaItemsFromUpComingMeeting(opt);

		}
		return result;
	}

	public static String requestAgendaItemsFromPastMeeting(int opt) {
		String result = "";
		try {
			OUT.write(7);
		} catch (Exception e) {
			connect(0);
			return requestAgendaItemsFromPastMeeting(opt);
		}
		try {
			OUT.write(opt);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestAgendaItemsFromPastMeeting(opt);
		}
		return result;
	}

	public static String requestAgendaItemsFromCurrentMeetings(int opt) {
		String result = "";
		try {
			OUT.write(21);
		} catch (Exception e) {
			connect(0);
			return requestAgendaItemsFromCurrentMeetings(opt);
		}
		try {
			OUT.write(opt);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestAgendaItemsFromCurrentMeetings(opt);
		}
		return result;
	}

	public static String requestResumeUpcumingMeeting(int opt) {
		String result = "";
		try {
			OUT.write(4);
		} catch (Exception e) {
			connect(0);
			return requestResumeUpcumingMeeting(opt);
		}
		try {
			OUT.write(opt);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestResumeUpcumingMeeting(opt);
		}
		return result;
	}

	public static String requestResumePastMeeting(int opt) {
		String result = "";
		try {
			OUT.write(5);

		} catch (Exception e) {
			connect(0);
			return requestResumePastMeeting(opt);

		}
		try {
			OUT.write(opt);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestResumePastMeeting(opt);

		}
		return result;
	}

	public static String requestResumeMesage(int opt) {
		String result = "";
		try {
			OUT.write(9);
		} catch (Exception e) {
			connect(0);
			return requestResumeMesage(opt);

		}
		try {
			OUT.write(opt);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestResumeMesage(opt);

		}
		return result;
	}

	public static String requestActionItemsPastMeeting(int opt) {
		String result = "";
		try {
			OUT.write(29);
		} catch (Exception e) {
			connect(0);
			return requestActionItemsPastMeeting(opt);

		}
		try {
			OUT.write(opt);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestActionItemsPastMeeting(opt);

		}
		return result;
	}

	public static String resquestChatFromItemPastMeeting(int optMeeting,
			int optItem) {
		String result = "";
		try {
			OUT.write(27);
		} catch (Exception e) {
			connect(0);
			return resquestChatFromItemPastMeeting(optMeeting, optItem);

		}
		try {
			OUT.write(optMeeting);
			OUT.write(optItem);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return resquestChatFromItemPastMeeting(optMeeting, optItem);

		}
		return result;
		// return
		// "Conversation: \n Stannis-> Davos give me my magic sword! \n2-> Davos-> here yougo you're grace... melessiandre as bee excpteing you yoy're grace";
	}

	public static boolean replyInvite(boolean decision) {
		try {
			OUT.writeBoolean(decision);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return replyInvite(decision);
		}
	}

	public static boolean requestAddItemToAgenda(int opt, String itemToadd) {
		try {
			OUT.write(11);
		} catch (Exception e) {
			connect(0);
			return requestAddItemToAgenda(opt, itemToadd);
		}
		try {
			OUT.write(opt);
			OUT.writeUTF(itemToadd);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestAddItemToAgenda(opt, itemToadd);

		}
	}

	public static boolean requestDeleteItemToAgenda(int optMeetenig,
			int itemToDelete) {
		try {
			OUT.write(12);
		} catch (Exception e) {
			connect(0);
			return requestDeleteItemToAgenda(optMeetenig, itemToDelete);
		}
		try {
			OUT.write(optMeetenig);
			OUT.write(itemToDelete);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestDeleteItemToAgenda(optMeetenig, itemToDelete);
		}
	}

	public static boolean requestMofifyItemToAgenda(int optMeeting,
			int optItemToModify, String newAgendaItem) {
		try {
			OUT.write(13);
		} catch (Exception e) {
			connect(0);
			return requestMofifyItemToAgenda(optMeeting, optItemToModify,
					newAgendaItem);
		}
		try {
			OUT.write(optMeeting);
			OUT.write(optItemToModify);
			OUT.writeUTF(newAgendaItem);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestMofifyItemToAgenda(optMeeting, optItemToModify,
					newAgendaItem);

		}
	}

	public static boolean requestAddKeyDecisionToAgendaItem(int optMeeting,
			int optItemToModify, String newKeyDecision) {
		try {
			OUT.write(14);
		} catch (Exception e) {
			connect(0);
			return requestAddKeyDecisionToAgendaItem(optMeeting,
					optItemToModify, newKeyDecision);
		}
		try {
			OUT.write(optMeeting);
			OUT.write(optItemToModify);
			OUT.writeUTF(newKeyDecision);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestAddKeyDecisionToAgendaItem(optMeeting,
					optItemToModify, newKeyDecision);

		}
	}

	public static boolean requestAddNewAcionItem(int opt, String newActionItem) {
		try {
			OUT.write(15);
		} catch (Exception e) {
			connect(0);
			return requestAddNewAcionItem(opt, newActionItem);
		}
		try {
			OUT.write(opt);
			OUT.writeUTF(newActionItem);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestAddNewAcionItem(opt, newActionItem);

		}
	}

	public static int requestSizeToDo() {
		try {
			OUT.write(16);
			return IN.read();
		} catch (IOException e) {
			connect(0);
			return requestSizeToDo();
		}
	}

	public static String requestActionItemsFromUser() {
		String result = "";
		try {
			OUT.write(17);
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

	public static boolean requestMarkActionAsDone(int optAction,
			boolean decision) {
		try {
			OUT.write(18);
			OUT.write(optAction);
			OUT.writeBoolean(decision);
			return IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			return requestMarkActionAsDone(optAction, decision);
		}
	}

	public static String requestCurrentMeetings() {
		String result = "";
		try {
			OUT.write(19);
			result = IN.readUTF();
		} catch (Exception e) {
			connect(0);
			return requestCurrentMeetings();
		}
		return result;
	}

	public static String requestResumeCurrentMeetings(int optCurrentMeeting) {
		String result = "";
		try {
			OUT.write(20);
		} catch (Exception e) {
			connect(0);
			return requestResumeCurrentMeetings(optCurrentMeeting);

		}
		try {
			OUT.write(optCurrentMeeting);
			result = IN.readUTF();
		} catch (IOException e) {
			connect(0);
			return requestResumeCurrentMeetings(optCurrentMeeting);
		}
		return result;
	}

	public static String requestMessagesFromAgendaItem(int optCurrentMeeting,
			int optItem) {
		String result = "";
		try {
			OUT.write(23);
		} catch (Exception e) {
			connect(0);
			return requestMessagesFromAgendaItem(optCurrentMeeting, optItem);
		}
		try {
			OUT.write(optCurrentMeeting);
			OUT.write(optItem);
			result = IN.readUTF();
		} catch (IOException e) {
			return requestMessagesFromAgendaItem(optCurrentMeeting, optItem);

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

	public static void requestLeaveChat(int optCurrentMeeting, int optItem) {
		try {
			OUT.write(26);
		} catch (Exception e) {
			connect(0);
			requestLeaveChat(optCurrentMeeting, optItem);
		}
		try {
			OUT.write(optCurrentMeeting);
			OUT.write(optItem);
			// IN.readBoolean();
		} catch (IOException e) {
			connect(0);
			requestLeaveChat(optCurrentMeeting, optItem);

		}
	}

	public static boolean requestInviteNewUser(int optmeeting, String username) {
		boolean success = false;
		try {
			System.out.println("28");
			OUT.write(28);
		} catch (Exception e) {
			System.out.println("cathc 27");
			connect(0);
			requestInviteNewUser(optmeeting, username);
		}
		try {
			System.out.println("optmeeting");
			OUT.write(optmeeting);
			System.out.println("username");
			OUT.writeUTF(username);
			success = IN.readBoolean();
			System.out.println("returnning " + success);
			return success;
		} catch (IOException e) {
			connect(0);
			requestInviteNewUser(optmeeting, username);

		}
		return false;
	}

	// -------------------------------------- AUXILIAR FUNCTIONS MENU

	public static void registerNewClient() {
		String userName, passWord, address, dob, phoneNumer, mail, finalInfo = "";
		boolean testName = false, testDob = false;
		System.out.println("Register new USER\n");
		SC.nextLine();
		do {
			System.out.println("Insert USER name:");
			userName = SC.nextLine();
			try {
				OUT.writeUTF(userName);
				testName = IN.readBoolean();
			} catch (IOException e) {
				connect(0);
			}
			if (testName) {
				System.out.println("Name already exists, try again\n");
			}
		} while (testName);
		System.out.println("PassWord: ");
		passWord = SC.nextLine();
		System.out.println("Address: ");
		address = SC.nextLine();
		do {

			System.out.println("Date of birthday (dd/mm/yyyy): ");
			dob = SC.nextLine();
			testDob = testDateOfBirthDay(dob);
			if (!testDob) {
				System.out.println("Wrong format, try again\n");
			}
		} while (!testDob);
		System.out.println("Phone number: ");
		phoneNumer = SC.nextLine();
		System.out.println("Email: ");
		mail = SC.nextLine();

		finalInfo = userName + "," + passWord + "," + address + "," + dob + ","
				+ phoneNumer + "," + mail;
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

	public static void chat(int optMeeting, int optagendaItem) {
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
				OUT.write(24);
				OUT.write(optMeeting);
				OUT.write(optagendaItem);
				OUT.writeUTF(textRecived);
				textRecived = null;
			} catch (IOException e) {
				connect(0);
				System.out.println(requestMessagesFromAgendaItem(optMeeting,
						optagendaItem));
				rt = new ReadingThread();
			}
		}
	}

	public static void creatNewMeeting() {
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
				System.out
						.println("Wrong format (min 0h:30m / max 2 years), try again");
			} else if (!pastDate) {
				System.out
						.println("Can't creat a meeting in the past (min 0h:30m / max 2 years), try again");
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
				System.out
						.println("One or more USER names do not exist, try again");
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
		request = responsible + "-" + desireOutCome + "-" + local + "-" + title
				+ "-" + date + "-" + guests + "-" + agendaItems + "-"
				+ duration;
		boolean success = requestServerNewMeeting(request);
		if (success)
			System.out.println("Meeting successfully created!");
		else
			System.out.println("Error creating meeting...");
	}

	public static void addItemstoAgenda(int opt) {
		String itemToDiscuss;
		System.out.println("Add items to agenda: ");
		System.out.println("Item to discuss: ");
		// SC.next();
		itemToDiscuss = SC.nextLine();
		boolean success = requestAddItemToAgenda(opt, itemToDiscuss);
		if (success)
			System.out.println("Agenda item was added successfully!!");
		else
			System.out.println("Error adding Item to Agenda....");
	}

	public static void subMenuDeleteItemstFromAgenda(int optMeeting) {
		int optItemtoDelete, size;
		String options = requestAgendaItemsFromUpComingMeeting(optMeeting), optString;
		String[] countOptions = options.split("\n");
		size = countOptions.length;
		options = options.replaceAll("Any other businness", "");
		do {
			for (int i = 0; i < size - 1; i++) {
				System.out.println(countOptions[i]);
			}
			System.out.println();
			System.out.println("0-> Back");
			System.out.print("Choose an option: ");
			optString = SC.nextLine();
			if (!isNumeric(optString)
					|| (Integer.parseInt(optString) < 0 || Integer
							.parseInt(optString) > (size - 1))) {
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
				System.out.println("Wrong option");
			}

		} while (!isNumeric(optString)
				|| (Integer.parseInt(optString) < 0 || Integer
						.parseInt(optString) > (size - 1)));
		optItemtoDelete = Integer.parseInt(optString);
		if (optItemtoDelete == 0) {
			return;
		}
		String deleteConfirm = "";
		do {
			System.out.println("Delete this item? (y/n)");
			deleteConfirm = SC.nextLine();
		} while (!deleteConfirm.equals("y") && !deleteConfirm.equals("n"));
		System.out.println("------------------");
		if (deleteConfirm.equalsIgnoreCase("y")) {
			boolean success = requestDeleteItemToAgenda(optMeeting,
					optItemtoDelete);
			if (success) {
				System.out.println("Agenda item was deleted successfully!!");
			} else {
				System.out.println("Error deleting Item from Agenda....");
			}
			System.out.println("Insert any key to return ");
			SC.next();
		}
	}

	public static void modifyNameFromAgendaItem(int optMeeting,
			int optItemtoModify) {
		String NewItemToDiscuss;
		System.out.println("New item to discuss: ");
		NewItemToDiscuss = SC.nextLine();
		boolean success = requestMofifyItemToAgenda(optMeeting,
				optItemtoModify, NewItemToDiscuss);
		if (success)
			System.out.println("Agenda item was modified successfully!!");
		else
			System.out.println("Error changing Item fom Agenda....");
	}

	public static void addNewKeyDecisionToAgendaitem(int optMeeting,
			int optItemtoAddKeyDecision) {
		String NewKeyDecision;
		System.out.println("New key Decision: ");
		NewKeyDecision = SC.nextLine();
		boolean success = requestAddKeyDecisionToAgendaItem(optMeeting,
				optItemtoAddKeyDecision, NewKeyDecision);
		if (success) {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
			System.out.println("Key decision added successfully!!");
		} else {
			System.out.println("Error ading key decision....");
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
		}
	}

	public static void addNewActionItem(int optMeeting) {
		String newActionItem = "", responsableUser = "";
		System.out.println("New ation Item: ");
		newActionItem = SC.nextLine();
		System.out.println("Responsable USER: ");
		responsableUser = SC.nextLine();
		boolean success = requestAddNewAcionItem(optMeeting, newActionItem
				+ "-" + responsableUser);
		if (success)
			System.out.println("Agenda item was added successfully!!");
		else
			System.out.println("Error adding Item to Agenda....");

	}

	// -------------------------------------- TEST DATA INPUT
	@SuppressWarnings("deprecation")
	public static boolean myDateTest(String date) { // receives
													// "dd/mm/yyyy hh:mm"
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
				} else if (day > 30
						&& (month == 4 || month == 6 || month == 9 || month == 11)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isLeapYear(int ano) {
		return (ano % 4 == 0);
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
			// System.out.println("testing-> " + g);
			if (!requestIfClientExists(g)) {
				// System.out.println("true");
				return false;
			}
		}
		// System.out.println("false");
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
				} else if (day > 30
						&& (month == 4 || month == 6 || month == 9 || month == 11)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}
}

class ReadingThread extends Thread {
	protected DataInputStream din;
	boolean isRunning;

	public ReadingThread() {
		this.din = Client.IN;
		isRunning = true;
		this.start();
	}

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
