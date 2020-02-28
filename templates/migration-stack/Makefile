packagesource:
	rm package/MigrationHelper.zip; cd src/MigrationHelper/; zip -r ../../package/MigrationHelper.zip .; cd ../..
shipsource:
	aws s3 cp package/MigrationHelper.zip s3://$(BUCKET)
createstack:
	aws cloudformation create-stack --stack-name mortha-migration-helper --template-body file://migration-helper.yml --parameters ParameterKey=HelperSourceBucket,ParameterValue=$(BUCKET) --capabilities CAPABILITY_IAM --disable-rollback
deletestack:
	aws cloudformation delete-stack --stack-name mortha-migration-helper
updatestack:
	aws cloudformation update-stack --stack-name mortha-migration-helper --template-body file://migration-helper.yml --parameters ParameterKey=HelperSourceBucket,ParameterValue=$(BUCKET) --capabilities CAPABILITY_IAM