from sypht.client import SyphtClient
import json
import os

sc = SyphtClient('2lcge0puhs9p629e5umq61q54n', 'v93g699ds5m6t0hbevcitmef1bgeh7008a4gs91eja75gf3mnsb')

for invoicename in os.listdir("PDFs"):

    print(invoicename)
    short = invoicename[:-4]
    print(short)

    with open("PDFs/" + invoicename, 'rb') as f:
        fid = sc.upload(f, products=["invoices"])

    with open(short + ".txt", "w") as json_file:
        json.dump(sc.fetch_results(fid), json_file, indent=4)

    json_file.close()

    # all the info is in the short.txt file

    details_file = open(short + "Details.txt", "w")

    with open(short + ".txt") as jsonfile:
        for line in jsonfile:
            if "document.referenceNo" in line:
                temp = line.strip()
                temp = temp[(temp.index(":") + 2):]
                details_file.write("Invoice Number: " + temp + "\n")

            if "issuer.name" in line:
                temp = line.strip()
                temp = temp[(temp.index(":") + 2):]
                details_file.write("Vendor Name: " + temp + "\n")

            if "issuer.address" in line:
                temp = line.strip()
                temp = temp[(temp.index(":") + 2):]
                details_file.write("Bill To: " + temp + "\n")

            if "document.date" in line:
                temp = line.strip()
                temp = temp[(temp.index(":") + 2):]
                details_file.write("Invoice Date: " + temp + "\n")

            if "invoice.total" in line:
                temp = line.strip()
                temp = temp[(temp.index(":") + 2):]
                details_file.write("Total Amount: " + temp + "\n")

    details_file.close()
