# Artificial-Intelligence-Project-NTUA

A project created for the Artificial Intelligence course at NTUA's school of Electrical and Computer Engineering. The project was written in Java using IntelliJ IDEA.

The project's goal is to calculate the best routes a number of taxis can take in order to go to a given location of a client and choose the taxi with the shortest route.

At first the application takes 3 CSV files as input.
* client.csv: Includes has the coordinates of the client's location. 
* taxis.csv: Includes the coordinates of the taxis' locations. 
* nodes.csv: Includes the coordinates and the id of the street each node belongs to. Nodes are used to form the streets of the given area.

The application uses the A* algorithm to calculate the best paths. There is an option in which the user can define a maximum number of elements that the A* search field can have. The default number of elements is 100. The application takes a new number through command line arguments.

As an output the application creates a TaxiRoutes.kml file in which the routes of all the taxis are located. The selected taxi's route always has green color while the rest have random colors.
