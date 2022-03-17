let
  pkgs = import <nixpkgs> {};

  lp_solve-java = import nix/lp_solve-java.nix pkgs;
in

pkgs.mkShell {
  name = "recherche";
  version = "0.0.1";

  buildInputs = with pkgs; [
    jdk11
    lp_solve-java
    jetbrains.idea-community
  ];

  LD_LIBRARY_PATH = "${lp_solve-java}/lib:$LD_LIBRARY_PATH";

  LPSOLVE_JAVA = "${lp_solve-java}/lib/lpsolve55j.jar";
  #CLASSPATH = "${lp_solve-java}/lib";
}
