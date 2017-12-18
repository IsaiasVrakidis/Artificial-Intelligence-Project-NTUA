# Artificial-Intelligence-Project-NTUA

A project created for Artificial Intelligence cource at Electrical and Computer Engineering school at NTUA. The project was written with Java in IntelliJ IDEA.
The projects goal is to calculate the best routes a number of taxis can take in order go to a given location of a client and to choose the taxi with the shortest route.
At first the application is taking as input 3 CSV files client.csv which has the coordinates of the clients location taxis.csv which has the coordinates of the taxis locations and nodes.csv which has the coordinates and the id of the street each node belongs too. Nodes are used to form the streets of the given area.
The application is using the A* algorithm to calculate the best paths. There is an option in which the user can define a maximum number of elements that the A* search field can have. The deafault number of elements is 100. The application will take a new number through command line arguements.
As an output the aplication will creat the TaxiRoutes.kml file in which there will be the routes of all the taxis. The selected taxi's route will have green color always, the rest will have random colors.
