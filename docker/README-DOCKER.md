# Running backed in docker

I have not yet had time to create a dockerfile which creates an image that contains both this app and the postgreSQL database, but the 'hctb-dockerfile' contains the instructions for building an image out of this app. though the environment variables must be changed to fit that of the postgreSQL docker container

To  build a docker image for this app docker needs to be installed on the computer.
 
First build a jar file with instructions from the project README.org

and then from this directory, run

    [sudo] docker build --tag imagename -f hctb-dockerfile ../target/uberjar/ 
   
then set up a postgres image and container.

    [sudo] docker pull postgres

    [sudo] docker run  --name postgres-container 
    --env POSTGRES_PASSWORD=kotoba -p 5432:5432 --detach postgres

which can be linked to by running `psql` in a seperate container using the following instruction.

    [sudo] docker run -it --rm 
    --link my-postgres-container:postgres postgres psql -h postgres -U postgres

then create the database

    CREATE DATABASE hctb;

   
and then (on a different terminal), run

    sudo docker run -it 
    --link postgre-container:postgres --env CSVDIR=/etc/bike-data
    --env POSTGRES_HOST=postgres-container 
    --mount type=bind,source=/path/to/data-dir,target=/etc/bike-data --rm imagename 


This should load the csv data from the given directory to the database hctb in the postgres container to be used by the front end. this can be checked from the psql terminal by typing

    SELECT COUNT(*) FROM subdir_or_filename_table
