import re
import os
import yaml
import json
import requests

base_url='http://school.openregister.org'

test_files_dir = 'testdata'
json_files_dir = 'jsons'
test_files = os.listdir(test_files_dir)

for test_file in test_files:
    path = '%s/%s' % (test_files_dir, test_file)
    with open(path) as reg_file:
        yaml_file = yaml.load(reg_file)
        data = json.loads(json.dumps(yaml_file))

        expected_response = data["response"]

        res=requests.get(base_url + data["request"]["path"])

        isTestPassed=True
        failure_reasons=[]

        actual_response_data=res.text

        if res.status_code!=expected_response["statuscode"]:
            isTestPassed=False
            failure_reasons.append('statuscode')

        if 'matches' in expected_response and expected_response['matches']!=None:
            if json.loads(expected_response['matches'])!=json.loads(actual_response_data):
                isTestPassed=False
                failure_reasons.append('matches')

        if 'contains' in expected_response and expected_response['contains']!=None:
            if expected_response['contains'] not in  actual_response_data:
                isTestPassed=False
                failure_reasons.append('contains')

        if 'matchRegex' in expected_response and expected_response['matchRegex']!=None:
            match=re.match(expected_response['matchRegex'], actual_response_data)
            if match==None:
                isTestPassed=False
                failure_reasons.append('matchRegex')

        if 'matchFileContents' in expected_response and expected_response['matchFileContents']!=None:
            path = '%s/%s' % (json_files_dir, expected_response['matchFileContents'])
            with open(path) as data_file:
                data_file_content = json.load(data_file)

                if data_file_content!=json.loads(actual_response_data):
                    isTestPassed=False
                    failure_reasons.append('matchFileContents')


        if isTestPassed==False:
            print("FAILED - Test: " + test_file + " : " + data['Description'])
            print ("Failure reasons are -> [ "+ ", ".join(failure_reasons) +" ]")
        else:
            print("PASSED - Test: " + test_file + " : " + data['Description'])

        print("")
