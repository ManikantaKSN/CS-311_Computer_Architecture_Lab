import sys
import os
import zipfile
import shutil
import filecmp
import subprocess
from threading import Timer

def main():
    if len(sys.argv) < 2:
        print("Usage: python script.py <zip_file>")
        sys.exit(1)

    zip_file = sys.argv[1]

    l = len(zip_file.split(os.sep))
    print(f"Students:\t{zip_file.split(os.sep)[l-1].split('_')[0]} and {zip_file.split(os.sep)[l-1].split('_')[1].split('.')[0]}\n")

    submissions_temp_dir = os.path.join(".", "submissions")

    if not os.path.exists(submissions_temp_dir):
        os.mkdir(submissions_temp_dir)

    zip_ref = zipfile.ZipFile(zip_file, 'r')
    zip_ref.extractall(submissions_temp_dir)
    zip_ref.close()

    shutil.copyfile("build.xml", os.path.join(submissions_temp_dir, "build.xml"))

    os.chdir(submissions_temp_dir)

    stdout_file = open("./tmp.output", 'a')
    popen_args = ["ant", "make-jar"]
    cmd = subprocess.list2cmdline(popen_args)
    proc = subprocess.Popen(cmd, stdout=stdout_file, stderr=stdout_file, shell=True)
    timer = Timer(5, proc.kill)
    try:
        timer.start()
        stdout, stderr = proc.communicate()
    finally:
        timer.cancel()
    stdout_file.close()

    if not os.path.exists(os.path.join("jars", "assembler.jar")):
        print("Compilation failed. JAR file not created.")
        sys.exit(0)

    test_cases_dir = os.path.join("..", "test_cases")
    total_marks = 0
    scored_marks = 0
    for testcase in os.listdir(test_cases_dir):
        if testcase.endswith(".asm"):
            total_marks += 1

            stdout_file = open("./tmp.output", 'a')
            popen_args = [
                "java", "-Xmx1g", "-jar", "jars/assembler.jar",
                os.path.join(test_cases_dir, testcase),
                os.path.join(".", f"{testcase.split('.')[0]}.observedoutput")
            ]
            cmd = subprocess.list2cmdline(popen_args)
            proc = subprocess.Popen(cmd, stdout=stdout_file, stderr=stdout_file, shell=True)
            timer = Timer(5, proc.kill)
            try:
                timer.start()
                stdout, stderr = proc.communicate()
            finally:
                timer.cancel()
            stdout_file.close()

            observed_output_file = os.path.join(".", f"{testcase.split('.')[0]}.observedoutput")
            expected_output_file = os.path.join(test_cases_dir, f"{testcase.split('.')[0]}.out")

            if os.path.exists(observed_output_file):
                if filecmp.cmp(expected_output_file, observed_output_file):
                    scored_marks += 1
                    print(f"{testcase} : PASS!")
                else:
                    print(f"{testcase} : FAIL - Incorrect object file contents")
            else:
                print(f"{testcase} : FAIL - File not created")

    os.chdir("..")
    shutil.rmtree(submissions_temp_dir)

    print(f"\nTotal score = {scored_marks} out of {total_marks}")

if __name__ == "__main__":
    main()
