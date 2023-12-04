LoadMe.start; // begin measuring time

s.waitForBoot {

    0.2.wait;
    // load a file by relative path
    // this file is in the quark HelpSource Dir
    LoadMe("relativeCodeFile1_exists.scd");
    // try a non-existing file
    LoadMe("relativeCodeFile2_notThere.scd");
    // paths will be standardized
    LoadMe("~/Desktop/absolutePathCodefile_notThere.scd");

    0.2.wait;
    LoadMe.end;
};