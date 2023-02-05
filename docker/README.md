# Running backed in docker

I have not yet had time to create a dockerfile which creates an image that contains both this app and the postgreSQL database, but the 'hctb-dockerfile' contains the instructions for building an image out of this app. though the environment variables must be changed to fit that of the postgreSQL docker container

to build a docker image for this app run first build a jar file and he from this directory, run

    [sudo] docker build --tag imagenamme -f hctb-dockerfile ../target/uberjar/ 
   
then set up a postgres image and container

    [sudo] docker pull postgres

    [sudo] docker run  --name postgres-container 
    --env POSTGRES_PASSWORD=kotoba -p 5432:5432 --detach postgres

which can be linked to by running `psql`

    [sudo] docker run -it --rm 
    --link my-postgres-container:postgres postgres psql -h postgres -U postgres

then create the database

    CREATE DATABASE hctb;

   
and then (on a different terminal) run

    sudo docker run -it 
    --link postgre-container:postgres --env CSVDIR=/etc/bike-data
    --env POSTGRES_HOST=postgres-container 
    --mount type=bind,source=/path/to/data,target=/etc/bike-data --rm hctb 


This should load the csv data from the given directory to the database hctb in the postgres container this can be checked from the psql terminal by 

    SELECT COUNT(*) FROM subdir_or_filename
