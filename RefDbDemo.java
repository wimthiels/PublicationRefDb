package publicationRefDb;


public class RefDbDemo {

	public static void main(String[] args) {
		
		addPublicationsToDB();

		checkDatabaseConsistency();
		printEntireRefDb();

		demoSearchOnDb();

		addReferencesToDB();

		demoRemovePublications();
		
		demoChangeAuthorsAndTitle();
		
		demoCitationScore();
		
		demoTransitiveClosure();
		
		System.out.println("\nTo end we show the state of the entire database again, and do a final consistency check : ");
 		printEntireRefDb();
		checkDatabaseConsistency();
		System.out.println("END");
	
		
		//
	
		
		
	}
	private static void demoChangeAuthorsAndTitle() {
		try {
		System.out.println("\nDEMO >>>> CHANGE AUTHORS AND TITLE :");
		System.out.println(
				"***************************************************************************************************************");
		System.out.println("\nNow let's change the authors and title of a publication and see that the database remains consistent (indexes are updated).");
		Publication pub9 = RefDb.getPublicationById("9");
		System.out.println("\nLet's take publication 9  : \n" +  pub9);
		
		System.out.println("Let's change the title to \"Perceived size of Targets Viewed From behind the Legs.  \n..and also remove author Adachi Kohei  and replace it with author Peter Thor :   " );
		
		pub9.removeAsAuthor("Kohei, Adachi");

		pub9.addAsAuthor("Thor, Peter");
		pub9.setTitle("Perceived size of Targets Viewed From behind the Legs");
		System.out.println("\nThe publication is updated as you can see : \n" +  pub9);
		
		checkDatabaseConsistency();
		
		} catch (AuthorNameNotValidException | InputFieldNotSpecifiedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	private static void demoTransitiveClosure() {
		System.out.println("\nDEMO >>>> TRANSITIVE CLOSURE CITATIONS :");
		System.out.println(
				"***************************************************************************************************************");
		System.out.println("\nLet's gather the transitive closure for the citations of publication 4.");
		System.out.println("This publication is cited by publication 1.  furthermore publication 1 is cited by publication 10 and 11. And finally publication 10 is cited by publication 5.");
		System.out.println("(caveat : publication 5 is cited by publication 1 : we must avoid a loop...)");
		System.out.println("So the transitive closure for publication 4 should be = 1, 10, 11, 5.  Let's see");
		for (Publication publication : RefDb.getTransitiveClosureCitedBy(RefDb.getPublicationById("4"))) {
			System.out.println("\npublication in the closure set\n\n" + publication);
			
		}
		
			
	
		
	}
	private static void demoCitationScore() {
		try {
		System.out.println("\nDEMO >>>> CITATION INDEX :");
		System.out.println(
				"***************************************************************************************************************");
		System.out.println("\nLet's calculate the citation index of Douglas Adams.   ");
		System.out.println("Douglas Adams is an author in publications 1 4 5 6 and  8.  (Caveat : Dirk Adams is also an author with the same indexkey (D. Adams)) ");
		System.out.println("publication 4 is cited by 1 (journal article) => score = 1");
		System.out.println("publication 5 is cited by 1 (journal article )=> score = 1");
		System.out.println("so citation index must be 2.0.  Let's check : ");

		System.out.println("-->" + RefDb.getCitationIndex("Adams, Douglas")+ "\n");
		
		System.out.println("Now add a reference from 10 to 1.  publication 10 is a conference paper, so that should raise the citation index by 0.7. Let's see  :");
		RefDb.addCitationReference("10", "1");
		System.out.println("-->" + RefDb.getCitationIndex("Adams, Douglas")+ "\n");
		System.out.println("Ok. now let's change the weight of the citation index of the conference paper to 0.6.  So we expect 2.6 as a result : ");
		PublicationType.CONFERENCEPAPER.setCitationWeight(0.6);
		System.out.println("-->" + RefDb.getCitationIndex("Adams, Douglas")+ "\n");
		
		System.out.println("Now add a reference from 11 to 1.  publication 11 is a book, so that should raise the citation index by 1.2. Let's see  :");
		RefDb.addCitationReference("11", "1");
		System.out.println("-->" + RefDb.getCitationIndex("Adams, Douglas")+ "\n");
		
		} catch (AuthorNameNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthorNotInDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PublicationIsNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IdNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IdNotInReferenceDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InputFieldNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	private static void demoRemovePublications() {
		// removing and adding
		// remove
		try {
			System.out.println("\nDEMO >>>> REMOVING FROM THE DATABASE :");
			System.out.println(
					"***************************************************************************************************************");
			System.out.println("The number of publications in the DB at the start is :  " + RefDb.getNbPublications());
			System.out.println("Now let's remove publication 2.  here are it's characteristics \n :  ");
			Publication pub2 = RefDb.getPublicationById("2");
			System.out.println(pub2);
			System.out.println("\nNow remove the publication with ID=2:  \n" );
			RefDb.removePublicationFromDb("2");
			System.out.println("\nThis is what publication 2 looks like AFTER removal from the DB:  \n" + pub2);
			System.out.println("\nSome checks to see if everything is cleaned up :  " );
			System.out.println("The number of publications in the DB is reduced to :  " + RefDb.getNbPublications());
			System.out.println("\nSearching for ID = 2 gives " + RefDb.getPublicationById("2"));
			// via author
			System.out.println("\nCheck that the authorindex is cleaned up :  so Search by authorname J. Bond gives this result: ");

			for (Publication pub : RefDb.getPublicationsByAuthorName("J. Bond")) {
				System.out.println(pub);
			}

			System.out.println("\nLikewise check the word index : Search by word in title : publications with the word \"of\" : ");
			for (Publication pub : RefDb.getPublicationsByTitleWord("of")) {
				System.out.println(pub);
			}
		} catch (AuthorNameIsNullException | WordIsNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}
	public static void demoSearchOnDb(){
		try {
		//do a search on the database
		System.out.println ("\nDEMO >>>> SEARCHING THE DATABASE :");
		System.out.println ("***************************************************************************************************************");
		//  via id
		System.out.println("Search by ID");
		System.out.println("------------");
		
		System.out.println("Let's search for the publication with ref id=7 : \n\n" + RefDb.getPublicationById("7"));
		
		//via author
		System.out.println();
		System.out.println("Search by author name index");
		System.out.println("---------------------------");
		System.out.println ("\nLet's search the publications of D. Adams : \n");

			for (Publication pub : RefDb.getPublicationsByAuthorName("D. Adams")) {
				System.out.println(pub);
			}

		
		//via word in title
			System.out.println();
			System.out.println("Search by word title index");
			System.out.println("---------------------------");
		System.out.println ("\nLet's search the publications with the word \"comparison\" : \n");
		for (Publication pub : RefDb.getPublicationsByTitleWord("comparison")) {
			System.out.println(pub);
		}
		System.out.println();
		System.out.println ("\nAnd the publications with the word \"from\" : \n");
		for (Publication pub : RefDb.getPublicationsByTitleWord("from")) {
			System.out.println(pub);
		}
		} catch (AuthorNameIsNullException | WordIsNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addReferencesToDB(){
		
		System.out.println("\nDEMO >>>> ADDING REFERENCES :");
		System.out.println(
				"***************************************************************************************************************");
		try {
			System.out.println("The following references are added : ");
			RefDb.addCitationReference("1", "2");
			System.out.println("1 cites 2");
			RefDb.addCitationReference("1", "3");
			System.out.println("1 cites 3");
			RefDb.addCitationReference("1", "4");
			System.out.println("1 cites 4");
			RefDb.addCitationReference("1", "5");
			System.out.println("1 cites 5");
			RefDb.addCitationReference("2", "3");
			System.out.println("2 cites 3");
			RefDb.addCitationReference("2", "4");
			System.out.println("2 cites 4");
			RefDb.addCitationReference("2", "5");
			System.out.println("2 cites 5");
			RefDb.addCitationReference("5", "10");
			System.out.println("5 cites 10");
			System.out.println("\nLet's check if this is correctly registered for publication 2 : ");
			System.out.println("\nPublication 2: ");
			System.out.println(RefDb.getPublicationById("2"));

		} catch (IdNotInReferenceDbException | InputFieldNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void addPublicationsToDB(){
 		
		System.out.println("DEMO >>>> ADDING PUBLICATIONS TO THE DATABASE :");
		System.out.println(
				"***************************************************************************************************************");
		try {
			System.out.println("\ncreating publications, and registering them in the database...\n");

			RefDb.addPublicationToDb(new JournalArticle("Apples and oranges.  a comparison",
					"Journal of Irreproducible Research", 45, 1999, "Adams,Douglas", "Adams,Douglas"));

			RefDb.addPublicationToDb(new JournalArticle("The morphology of Steve", "Journal of Irreproducible Research",
					78, 1905, "Adams,Douglas", "Bond, Jill"));

			RefDb.addPublicationToDb(new JournalArticle("Stock Market Behavior Predicted by Rat Neurons",
					"Journal of Neurology", 78, 1905, "Dhoore, Paul", "Adams, Dirk"));
			RefDb.addPublicationToDb(new JournalArticle("Questions From the Chinese Translator", "Contemplations", 78,
					1905, "Yin, Lee", "Yin, Toa", "Toa, Lin", "Adams, Douglas"));
			RefDb.addPublicationToDb(new ConferencePaper("Feline Reactions to Bearded Men", 1978,
					"Annals of Improbable research", "Adams,Douglas"));
			RefDb.addPublicationToDb(new ConferencePaper("Feline Reactions to Bearded Men of beardtype 5#78#2", 1978,
					"Annals of Improbable research", "Adams,Douglas"));
			RefDb.addPublicationToDb(
					new Book("Mathematically Correct Breakfast: How to Slice a Bagel into Two Linked Halves", 2015,
							"Elsevier", "Adams,Dirk", "Kirk, Douglas"));
			RefDb.addPublicationToDb(
					new Book("Apples and oranges.  a comparison", 1999, "Adams,Douglas", "Adams,Douglas"));
			RefDb.addPublicationToDb(
					new JournalArticle("Perceived size and Perceived Distance of Targets Viewed From Between the Legs ",
							"Evidence for Proprioceptive Theory", 45, 2000, "Atsuki, Higashiyama", "Kohei, Adachi"));
			RefDb.addPublicationToDb(new ConferencePaper(
					"solving the problem of excessive automobile pollution emissions by Software design", 2003,
					"Volkswagen conference", "Malik,Peter"));
			RefDb.addPublicationToDb(
					new Book("The Need for Double-Strength Placebos", 2014, "de bezige Bij", "Mayer, Bill"));
			System.out.println("\nThe number of publications inserted in the DB :  " + RefDb.getNbPublications());

			System.out.println("\n --> Extra  : ");

			System.out.println("an attempt to register a similar publication should fail.  First instantiate a clone of publication1 : \n");
			Publication cloneOfPub1 = new JournalArticle("Apples and oranges.  a comparison",
					"Journal of Irreproducible Research", 45, 1999, "Adams,Douglas", "Adams,Douglas");
			System.out.println(cloneOfPub1);
			System.out.println(" \nand now try to add this to the DB ..  : ");
			
			try {
			RefDb.addPublicationToDb(cloneOfPub1);}
			catch (DuplicateEntryRefDbException e) {
				System.out.println("A DuplicateEntryRefDbException is catched.  OK. It works.");
			}
			
			
			
			System.out.println();
			System.out.println("\n --> Extra  : ");
			System.out.println("Not all publications must be registered in the database. As an illustration  : \n"
					+ (new Book("Horse Calculus", 2014, "JIR", "Adams, Douglas")));
			

		} catch (DuplicateEntryRefDbException | InputFieldNotSpecifiedException | InputFieldNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void printEntireRefDb() {
		System.out.println ("\nA FULL DUMP OF THE ENTIRE DATABASE :");
		System.out.println ("***************************************************************************************************************");
		RefDb.printRefdb();
	}
	public static void checkDatabaseConsistency (){
		System.out.println();
		System.out.println("database consistency checks ...");
		if (RefDb.hasProperIdTable()) System.out.println("-->hasProperIdTable OK!");
		else System.out.println("hasProperIdTable  NOT !");
		if (RefDb.hasProperAuthorIndex()) System.out.println("-->hasProperAuthorIndex OK!");
		else System.out.println("hasProperauthorIndex  NOT !");
		if (RefDb.hasProperTitleWordIndex()) System.out.println("-->hasProperTitleWordIndex OK!");
		else System.out.println("hasProperTitlewordindex NOT !");
		if (RefDb.hasProperPublications()) System.out.println("-->hasProperPublications OK!");
		else System.out.println("hasProperPublications NOT !");
		System.out.println();
	}

}
