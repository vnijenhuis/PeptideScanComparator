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

-combined D:\LundRawAnalysis\1D25CM\CombinedmRNAseq\ D:\LundRawAnalysis\1D50CM\CombinedmRNAseq\
-individual D:\LundRawAnalysis\1D25CM\IndividualmRNAseq\ D:\LundRawAnalysis\1D50CM\IndividualmRNAseq\
-uniprot D:\LundRawAnalysis\1D25CM\Uniprot\ D:\LundRawAnalysis\1D50CM\Uniprot\
-psm "DB search psm.csv"
-out C:\Users\f103013\Documents\vnijenhuis_docs\1D2DCombined\Output\
-threads 4
-target COPD
-control Control


The folders D:\LundRawAnalysis\1D25CM\CombinedmRNAseq\ D:\LundRawAnalysis\1D25CM\IndividualmRNAseq\ and
D:\LundRawAnalysis\1D25CM\Uniprot\ should contain folders with sample names such as :

    D:\LundRawAnalysis\1D25CM\Uniprot\COPD1\
    D:\LundRawAnalysis\1D25CM\Uniprot\COPD2\

These folders should contain the given psm file:

    D:\LundRawAnalysis\1D25CM\Uniprot\COPD1\DB search psm.csv
    D:\LundRawAnalysis\1D25CM\Uniprot\COPD2\DB search psm.csv

The -target and -control parameters should correspond to the initial name (without sample numbers) of the samples that are present:
currently works with only 2 samples. 
