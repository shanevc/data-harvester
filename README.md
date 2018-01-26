Build Docker Image  
  
	docker image build -t svancoller/data-harvester .
  
Run Docker

	docker container run --name data-harvester --rm -d -p 8083:8080 svancoller/data-harvester
	
Follow logs
	
	docker container logs -f data-harvester
	
