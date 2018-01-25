Build Docker Image  
  
	docker image build -t data-harvester-0.1 .
  
Run Docker

	docker container run --name data-harvester --rm -d -p 8080:8080 data-harvester-0.1
	
Follow logs
	
	docker container logs -f data-harvester
	
