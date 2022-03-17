{ fetchurl, stdenv, unzip, lp_solve, ... }:

let
  version = "5.5.2.11";

  lp_solve_dev = stdenv.mkDerivation {
    name = "lp_solve_dev";
    inherit version;

    src =
      fetchurl {
        url = "mirror://sourceforge/project/lpsolve/lpsolve/${version}/lp_solve_${version}_dev_ux64.tar.gz";
        sha256 = "0f1zwgsblyrqd9g9232y0gh3nzwlfprw6pf5snh57klz2sprbb6z";
      };

    setSourceRoot = "sourceRoot=`pwd`";

    installPhase = '':
      mkdir -p $out

      cp * $out
    '';
  };

in
stdenv.mkDerivation rec {
  name = "lp_solve-java-api";
  version = "5.5.2.11";

  src =
    fetchurl {
      url = "mirror://sourceforge/project/lpsolve/lpsolve/${version}/lp_solve_${version}_java.zip";
      sha256 = "1jfnl849i8fbfv3pmqspp6x1piq8ssyfgrg4bldzb564w0hwsk8a";
    };

  nativeBuildInputs = [ unzip ];

  buildInputs = [ lp_solve_dev ];

  installPhase = ''
    mkdir -p $out/lib

    cp ${lp_solve_dev}/*.so $out/lib
    #ln -s ${lp_solve}/lib/liblpsolve55.so $out/lib/liblpsolve55.so
    cp lib/ux64/liblpsolve55j.so $out/lib
    cp lib/lpsolve55j.jar $out/lib
  '';
}
