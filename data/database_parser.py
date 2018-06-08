import json
from pprint import pprint
import csv
import collections
import sys

participant_count = 10
turn_count = 5
csv_file_path = "output.csv"

def to_string(s):
    try:
        return str(s)
    except:
        #Change the encoding type if needed
        return s.encode('utf-8')

if __name__ == "__main__":
    with open('data.json') as f:
        data = json.load(f)

    processed_DDR_data = []

    for x in range(15, 17):
        for turn in range(0, 5):
            value = data[str(x)]["screen"]["DDR"][str(turn)]["completionTime"]
            tup1 = ("User_" + str(x) + "_DDR_completionTime" , value )
            processed_DDR_data.append(tup1)
       
    #pprint(value)
    #pprint(processed_DDR_data)

    with open(csv_file_path, 'w+') as f:
        #fieldnames = ['measurement_name' , 'measurement']
        writer = csv.writer(f)
        
        for row in processed_DDR_data:
            pprint(row)
            writer.writerow(row)
