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

        processed_DDR_data = collections.defaultdict(list)

        value = data["0"]["screen"]["DDR"]["0"]["completionTime"]

        for x in xrange(1, participant_count):
            for turn in xrange(0, turn_count):
                value = data[str(x)]["screen"]["DDR"][str(turn_count)]["completionTime"]
                processed_DDR_data["User_" + str(x) + "_DDR_completionTime"].append(value)
       

        pprint(value)
        pprint(processed_data)

        # with open(csv_file_path, 'w+') as f:
        #     writer = csv.DictWriter(f, header, quoting=csv.QUOTE_ALL)
        #     writer.writeheader()
        #     for row in processed_DDR_data:
        #         writer.writerow(row)
