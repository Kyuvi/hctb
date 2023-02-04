# hctb

Helsinki city bike app 

The final aim of this project is to create a web app to view the city-bike stations and the data of the journeys between those stations.

At this point only the backend is finalised (in Clojure) with the fronend intended to be written in clojurescript

The app expects a running postgreSQL database and csv files containing the journey and staion data to be located in a directory on the computer running it. The directory should contain csv files containing data of either the bike journeys or bike stations either loose or organised into sub-directories that the database tables should be based on. 

The backend reads the csv files from said folder and loads the data into the PostgreSQL database. if the given directory (which defaults to /tmp/bike-data on a Linux machine)
contains sub-directories it creates a table based on the name of the sub-directory with columns based on the data from the (first line of the) first file in the directory and loading the data from that file into the database validating that the data of each row element is of the correct type for the corressponding column . It then tries to load data from subsequent files in that directory into the same table. if a file in the sub-directory has a different data set (with a different column count) it creates a table based on that file name (overwiting/dropping any previous tables created with the same name) and loads the data into that table.

If the directory contains loose files, it creates a table named after each file and loads that file data into it.

### Data 
It validates the data based on the following criteria, if the criteria is not met by one element of the row, the whole row is discarded.

- timestamps are valid timestamps without timezones and the return time is after the departure time.

- journeys are have a duration in integers longer than 10 seconds and cover a distance more than 10 meters.

- longtitude and latitude are valid floating point numbers.

- Ids are positive integers.

- There are no empty strings (strings contining only spaces are recognized as valid).

This app has only been tested and used on a machine running linux with postgreSQL (running in docker). The default directory and database options reflect this. There is a possibility to change the defaults (see usage below) but I have not tested them on another OS yet.

## Installation

Download from http://example.com/FIXME.

## Usage
Can be run directly from the command line with `lein run`

or after building a jar file with 

    $ java -jar hctb-0.1.0-standalone.jar [arg]
    
It accepts one argument which should be a string of a (clojure) map consisting of one or more of the following keyword and string pairs
    
    :csvdir "/path/to/csvdir"
    :dbname "dbname"
    :user "user"
    :password "password"
    
NOTE: The password is not hidden and will be visible to anyone looking and with access to your history!


If those are not given, it then checks the system for the following environment variables

    CSVDIR 
    POSTGERS_DB
    POSTGRES_USER
    POSTGRES_PASS
    
For the directory database name, database user, and database password respectively

if it finds none it reverts to the defaults specified in the source code

which are 

    :csvdir "/tmp/bike-data"
    :dbname "hctb"
    :user "postgres"
    :password "test"

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Known Bugs

Passing a password with an empty string will cause an error when using the postgreSQL database. This seems to be an issue between clojure.java.jdbc and the postgreSQL database, with the database not accepting a db-spec with an empty string as a password.

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2023 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
