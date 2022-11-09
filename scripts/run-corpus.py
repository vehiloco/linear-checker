import argparse
import os
import shlex
import subprocess
import sys

import yaml


# python run-corpus.py --corpus-file <absPath> --executable <absPath>


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--corpus-file', dest='corpus_file', required=True)
    parser.add_argument('--executable', dest='executable', required=True)
    args = parser.parse_args()

    corpus_name = os.path.splitext(os.path.basename(args.corpus_file))[0]
    corpus_dir = os.path.dirname(os.path.abspath(args.corpus_file))

    benchmark_dir = os.path.join(corpus_dir, corpus_name)

    print("----- Fetching corpus... -----")
    if not os.path.exists(benchmark_dir):
        print("Creating corpus dir {}.".format(benchmark_dir))
        os.makedirs(benchmark_dir)
        print("Corpus dir {} created.".format(benchmark_dir))

    print("Loading corpus file...")
    with open(args.corpus_file) as projects_file:
        projects = yaml.load(projects_file, Loader=yaml.FullLoader)["projects"]
    print("Loading corpus file done.")

    print(projects)

    print("Enter corpus dir {}.".format(benchmark_dir))
    os.chdir(benchmark_dir)

    for project_name, project_attrs in projects.items():
        project_dir = os.path.join(benchmark_dir, project_name)
        if not os.path.exists(project_dir):
            git("clone", project_attrs["giturl"], "--depth", "1")

    print("----- Fetching corpus done. -----")

    print("----- Running Executable on corpus... -----")

    failed_projects = list()

    assemble_check_cmd = "./gradlew assembleCheckTypes"

    for project_name, project_attrs in projects.items():
        project_dir = os.path.join(benchmark_dir, project_name)
        os.chdir(project_dir)
        print("Enter directory: {}".format(project_dir))
        if project_attrs["clean"] == '' or project_attrs["build"] == '':
            print("Skip project {}, as there were no build/clean cmd.".format(project_name))
        print("Cleaning project...")
        subprocess.call(shlex.split(project_attrs["clean"]))
        print("Cleaning done.")

        # As there is a problem with build tool 'do-like-javac',
        # use './gradlew assembleCheckTypes' instead.
        # print "Running command: {}".format(tool_executable + " " + project_attrs["build"])
        # rtn_code = subprocess.call([tool_executable, project_attrs["build"]])

        print("Running command: {}".format(assemble_check_cmd))
        rtn_code = subprocess.call(shlex.split(assemble_check_cmd))
        print("Return code is {}.".format(rtn_code))
        if not rtn_code == 0:
            failed_projects.append(project_name)

    if len(failed_projects) > 0:
        print("----- Executable failed on {} out of {} projects. Failed projects are: {} -----".format(
            len(failed_projects), len(projects), failed_projects))
    else:
        print("----- Executable succeed typechecking all {} projects. -----".format(len(projects)))

    print("----- Running Executable on corpus done. -----")

    rtn_code = 1 if len(failed_projects) > 0 else 0

    sys.exit(rtn_code)


def git(*args):
    return subprocess.check_call(['git'] + list(args))


if __name__ == "__main__":
    main()
