##############################
#   Peptide Scan Collector   #
##############################


##############################
#         Parameters         #
##############################

-uniprot    Requires a path to the uniprot mRNASeq data that contain sample fodlers.    (example: D:/LundRawAnalysis/Dataset/Uniprot/)
-individual Requires a path to the individual mRNASeq data that contain sample fodlers.    (example: D:/LundRawAnalysis/Dataset/IndividualmRNASeq/)
-combined   Requires a path to the combined mRNASeq data that contain sample fodlers.    (example: D:/LundRawAnalysis/Dataset/CombinedmRNASeq/)
-psm        Name of the peptide spectrum match csv file                                 (example: "DB search psm.csv" or DB_search_psm.csv). 
                                                                                        Use Quotes if whitespaces are present in the file name.
-out        Path to write the output data to.
-threads    Amount of threads to use for multi-threading. Default is 1 thread.          (example: 4)
-target     Name of the target sample. This parameter is case sensitive.                (example: COPD)
-control    Name of the control sample. This parameter is case sensitive.               (example: Control)

##############################
#      Example command       #
##############################

-in D:\LundRawAnalysis\1D25CM\CombinedmRNAseq\ D:\LundRawAnalysis\1D50CM\CombinedmRNAseq\ D:\LundRawAnalysis\2DLCMSMS\CombinedmRNASeq\
-psm "DB search psm.csv"
-db C:\Users\f103013\Documents\vnijenhuis_docs\Fasta\uniprot\uniprot_taxonomy_3A9606.fasta
-cdb C:\Users\f103013\Documents\vnijenhuis_docs\Fasta\COPDHealthy\COPD19-test.fa
-idb C:\Users\f103013\Documents\vnijenhuis_docs\Fasta\COPDHealthy\
-out C:\Users\f103013\Documents\vnijenhuis_docs\1D2DCombined\Output\
-threads 4
-target COPD
-control Control

##############################
#        Folder layout       #
##############################

Some data will need a specific layout for this project to work properly.

The -in parameter requires a path to a given dataset. The method name, path name and sample name are derived from this parameter:
The folder should look like this: 

    \home\name\LundAnalysis\Dataset\RNASeqMethod\

This file should contain folders with sample names such as:

    COPD1, COPD_1
    Control1, Control_1

the -psm parameter should provide a psm csv file which is present in each sample folder.
    example: "DB search psm.csv"

The -db parameter should contain a path and file name of a given public database to filter out peptide sequences.
The best option for this process would be the Uniprot public protein database.
    
    D:\uniprot\uniprot_taxonomy_3A9606.fasta

The -cdb parameter is similar to the -db parameter, except that it requires a combined protein database fasta file of each sample.
    
    D:\combined\COPD19-database.fasta

The -idb requires a path to the individual protein database fasta files of each sample. (Reads multiple files!)
The name of the samples given at the -target and -control are used to determine which fasta file matches to which psm file.
For example the file Control_7_02_7829_database.fa
The -target/-control parameters define the regular expression used to determine if a sample name matches to the sample database.
by providing -target or -control with the value Control it is able to match the database to the right sample.

The -out parameters requires an EXISTING path to write output to.
Make sure to create the given path before starting this program, otherwise an error will appear.

The -threads parameter defines the amount of threads that the program should use.
The amount of threads can be found by calculating the following:

    amount of sockets * Cores per socket * threads per core

By using more cores the database matching will go a lot faster.

The -control and -target parameter are used to determine the amount of samples, link the right individual fasta to each sample and
to determine the index of each sample. These two parameters are case sensitive.
If, for example, the sample names COPD1 -> COPD10 and Healthy1 -> Healthy10 are used then the following input will suffice:
    -target COPD
    -control Healthy