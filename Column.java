package com.example.danie.rccolumndetailer;

/**
 * Created by danie on 14/02/2017.
 */

public class Column {
    double xDim;
    double yDim;
    int bx;
    int by;
    int bd;
    int fs;
    int fc;
    int cover;
    int mTieSize;
    int mTieSpacing;
    double eC;
    double eS;
    double ES;
    double betaD;
    double effectiveLength;
    double mColumnLength;
    String sectionType = "";
    String bracingType = "";

    public Column(double xDim, double yDim, int bx, int by, int bd, int fs, int fc, int cover, double betaD,
                  double effectiveLength) {
        this.xDim = xDim;
        this.yDim = yDim;
        this.bx = bx;
        this.by = by;
        this.bd = bd;
        this.fs = fs;
        this.fc = fc;
        this.cover = cover;
        this.eC = 0.003;
        this.eS = 0.0025;
        this.ES = 200000;
        this.betaD = betaD;
        this.effectiveLength = effectiveLength;
        this.mColumnLength = effectiveLength + 500; //This needs to be updated
        this.mTieSize = 10; //This needs to be updated
        this.mTieSpacing = 300; //This needs to be updated
        this.sectionType = "rectangular";
        this.bracingType = "braced";
    }

    public double columnTension() {
        int nosBars = 2 * bx + 2 * by - 4;
        double colNut = -1.0 * nosBars * Math.PI * Math.pow(bd, 2) / 4 * fs / 1000;
        return colNut;
    }

    public double columnSquash() {
        int nosBars = 2 * bx + 2 * by - 4;
        double alpha1 = Math.max(0.72, Math.min(0.85, 1.0 - 0.003 * fc));
        double aGross = xDim * yDim;
        double aSteel = nosBars * Math.PI * Math.pow(bd, 2.0) / 4.0;
        double aConc = aGross - aSteel;
        double colSquash = (alpha1 * fc * aConc + aSteel * fs) / 1000.0;
        return colSquash;
    }

    public double[][] columnReo() {
        double[][] colReo = new double[by][3];
        double firstBar;
        double lastBar;
        double barSpacing;

        if (by == 1) {
            firstBar = yDim / 2.0;
            barSpacing = 0;
        } else {
            firstBar = cover + bd / 2.0;
            lastBar = yDim - cover - bd / 2.0;
            barSpacing = (lastBar - firstBar) / (by - 1.0);
        }

        for (int i = 0; i < by; i++) {
            if (by == 1) {
                colReo[0][0] = 1;
                colReo[0][1] = firstBar + i * barSpacing;
                colReo[0][2] = bx * Math.PI * Math.pow(bd, 2) / 4.0;
            } else if (by != 1 && (i == 0 || i == (by - 1))) {
                colReo[i][0] = bx;
                colReo[i][1] = firstBar + i * barSpacing;
                colReo[i][2] = bx * Math.PI * Math.pow(bd, 2) / 4.0;
            } else if (i != 0 && i != (by - 1) && bx > 1) {
                colReo[i][0] = 2;
                colReo[i][1] = firstBar + i * barSpacing;
                colReo[i][2] = 2 * Math.PI * Math.pow(bd, 2) / 4.0;
            }
        }
        return colReo;
    }

    public double[] columnSolverKu(double ku) {
        double[][] colReo = this.columnReo();
        double d = colReo[colReo.length - 1][1];
        double kuD = ku * d;
        double alpha2 = Math.max(0.67, Math.min(0.85, 1.0 - 0.003 * fc));
        double gamma = Math.max(0.67, Math.min(0.85, 1.05 - 0.007 * fc));

        double sectAxialForce = 0.0;
        double sectMoment = 0.0;
        int barRows = colReo.length;

        double barZ;
        double barZp;
        double barAs;
        double barStrain;
        double barStress;
        double barForce;
        double barMoment;

        double concForceSub;
        double concMomentSub;

        double gammaKuD = gamma * kuD;
        double concZp = yDim / 2.0 - gammaKuD / 2.0;
        double concAg = gammaKuD * xDim;
        double alpha2Fc = alpha2 * fc;
        double concForce = concAg * alpha2Fc;
        double concMoment = concForce * concZp;
        sectAxialForce += concForce;
        sectMoment += concMoment;

        for (int i = 0; i < barRows; i++) {
            barZ = colReo[i][1];
            barZp = yDim / 2.0 - colReo[i][1];
            barAs = colReo[i][2];
            barStrain = eC / kuD * (kuD - barZ);
            barStress = Math.min(fs, Math.max(-fs, ES * barStrain));
            barForce = barAs * barStress;
            barMoment = barForce * barZp;
            if (barStrain >= 0) {
                concForceSub = barAs * -alpha2Fc;
                concMomentSub = concForceSub * barZp;
                sectAxialForce += concForceSub;
                sectMoment += concMomentSub;
            }
            sectAxialForce += barForce;
            sectMoment += barMoment;
        }

        sectAxialForce = sectAxialForce / 1000.0;
        sectMoment = sectMoment / 1000000.0;

        double[] sectionCap = {sectAxialForce, sectMoment};

        return sectionCap;
    }

    public double[][] colInteractionPoints() {
        double[][] interactionPoints = new double[202][3];
        double colSquash = this.columnSquash();
        double colTension = this.columnTension();
        double[][] colReo = this.columnReo();
        interactionPoints[0][0] = -1.0;
        interactionPoints[0][1] = colTension;
        interactionPoints[0][2] = 0.0;
        interactionPoints[1][0] = 0.0001;
        interactionPoints[1][1] = this.columnSolverKu(0.0001)[0];
        interactionPoints[1][2] = this.columnSolverKu(0.0001)[1];
        double ku;
        for (int i = 1; i <= 100; i++) {
            ku = (double) i / 100.0;
            interactionPoints[i + 1][0] = (double) i / 100.0;
            interactionPoints[i + 1][1] = this.columnSolverKu(i / 100.0)[0];
            interactionPoints[i + 1][2] = this.columnSolverKu(i / 100.0)[1];
        }

        double axialIncrement = colSquash - interactionPoints[101][1];
        double momentDecrease = -interactionPoints[101][2];
        double axialForceK1 = interactionPoints[101][1];
        double momentK1 = interactionPoints[101][2];

        for (int i = 1; i < 100; i++) {
            ku = Math.round((1 + (double) i / 100.0) * 100) / 100.0;
            interactionPoints[101 + i][0] = ku;
            interactionPoints[101 + i][1] = axialForceK1 + (double) i / 100.0 * axialIncrement;
            interactionPoints[101 + i][2] = momentK1 + (double) i / 100.0 * momentDecrease;
        }
        interactionPoints[201][0] = 2.0;
        interactionPoints[201][1] = colSquash;
        interactionPoints[201][2] = 0.0;
        return interactionPoints;
    }

    public double[] kuSolverPureMoment() {
        double ku = 0.0001;
        double axialForce = this.columnTension();
        int i = 1;
        while (axialForce < 0 && i < 10000) {
            ku = (double) i / 10000.0;
            axialForce = this.columnSolverKu(ku)[0];
            i++;
        }
        double kuPureMoment = (double) i / 10000.0;
        double momentCap = this.columnSolverKu(kuPureMoment)[1];
        double[] capacity = new double[2];
        capacity[0] = kuPureMoment;
        capacity[1] = momentCap;

        return capacity;
    }

    public void printColTension() {
        double roundedNut = Math.round(this.columnTension() * 10) / 10.0;
        System.out.println("Column Tension Capacity = " + roundedNut + "kN");
    }

    public void printColSquash() {
        double roundedNuc = Math.round(this.columnSquash() * 10) / 10.0;
        System.out.println("Column Squash Capacity = " + roundedNuc + "kN");
    }

    public void printColMoment() {
        double roundedKu = Math.round(this.kuSolverPureMoment()[0] * 100) / 100.0;
        double roundedMuc = Math.round(this.kuSolverPureMoment()[1] * 10) / 10.0;
        System.out.println("Column Moment Capacity = " + roundedMuc + "kN-m; ku = " + roundedKu);
    }

    public void printColReo() {
        double[][] colReoData = this.columnReo();
        int reoRow;
        int nosBars;
        double reoYDim;
        double reoArea;
        for (int i = 0; i < colReoData.length; i++) {
            reoRow = i + 1;
            nosBars = (int) colReoData[i][0];
            reoYDim = Math.round(10 * colReoData[i][1]) / 10.0;
            reoArea = Math.round(10 * colReoData[i][2]) / 10.0;
            System.out.println("Reo row " + reoRow + ": " + nosBars +
                    "/" + bd + "mm Dia bars: depth " + reoYDim + "mm: As " +
                    reoArea + "sq.mm");
        }
    }

    public void printColSectCapacity() {
        double[][] tempCapHolder = this.colInteractionPoints();
        double roundedAxialCap;
        double roundedMomentCap;
        double tempKu;
        for (int i = 0; i < tempCapHolder.length; i++) {
            tempKu = tempCapHolder[i][0];
            roundedAxialCap = Math.round(10 * tempCapHolder[i][1]) / 10.0;
            roundedMomentCap = Math.round(10 * tempCapHolder[i][2]) / 10.0;
            System.out.println("Point " + (i + 1) + ": ku = " + tempKu + ": Axial = " + roundedAxialCap + "kN: Moment = " +
                    roundedMomentCap + "kN-m");
        }
    }

    /**
     * @return radius of gyration of the section
     */
    public double concreteR() {
        if (sectionType.equals("circular")) {
            return 0.25 * yDim;
        } else if (sectionType.equals("rectangular")) {
            return 0.3 * yDim;
        } else {
            return 0.01 * yDim;
        }
    }

    public double concElasticCritBuckling() {
        double colDeadLoad = 1.0;
        double colLiveLoad = (1.0 - betaD) / betaD;
//        double betaD = deadLoad / (deadLoad + liveLoad);
        double[][] colReo = this.columnReo();
        double effectiveD = colReo[colReo.length - 1][1];
        double[] sectionBalanceCap = columnSolverKu(0.545);
        double balanceMomentCap = sectionBalanceCap[1] * 1000000.0;
        double criticalBucklingLoad = (Math.pow(Math.PI, 2.0) / Math.pow(effectiveLength, 2.0)) *
                (182.0 * effectiveD * 0.6 * balanceMomentCap / (1.0 + betaD)) / 1000.0;
        return criticalBucklingLoad;
    }

    public double columnKm(double moment1, double moment2) {
        return Math.max(0.4, Math.min(1.0, 0.6 - 0.4 * moment1 / moment2));
    }

    public double columnDeltaB(double colKm, double axialN, double colCriticalLoad) {
        return Math.max(1.0, colKm / (1 - axialN / colCriticalLoad));
    }

    /**
     * Calculates the column slender limit
     *
     * @param axialN
     * @return
     */
    public double columnSlender(double axialN, double moment1, double moment2) {
        double colSquash = this.columnSquash();
        double alphaC = 0;
        double stockyLimit = 22.0;
        if (axialN <= 0.0) {
            axialN = 0.1;
        }
        if (bracingType.equals("braced")) {
            if ((axialN / (0.6 * colSquash)) >= 0.15 && (axialN / (0.6 * colSquash)) < 0.9) {
                alphaC = Math.sqrt(2.25 - 2.5 * axialN / (0.6 * colSquash));
            } else if ((axialN / (0.6 * colSquash)) < 0.15) {
                alphaC = Math.sqrt(1 / (3.5 * axialN / (0.6 * colSquash)));
            } else if (axialN / (0.6 * colSquash) >= 0.9) {
                alphaC = 0;
            }
            stockyLimit = Math.max(25.0, alphaC * (38.0 - 15.0 / fc) * (1.0 + moment1 / moment2));
        }
        return stockyLimit;
    }

    public double columnCapacitySolver() {
        double colAxialCapacity = 0.1;
//        double colBetaD = betaD;
        double colDeadLoad = 1.0;
        double colLiveLoad = (1.0 - betaD) / betaD;
        double colEffectiveLength = effectiveLength;
        double colRadius = this.concreteR();
        double colSlenderness = colEffectiveLength / colRadius;
        double[][] colInteraction = colInteractionPoints();
        double colElasticBuckCap = this.concElasticCritBuckling();
        double mColKm = columnKm(-1.0, 1.0);
        double colDeltaB = 1.0;
        double colMagnifiedMinMoment;
        int compressionPoints = 0;
        int minKuPoint = 202;
        int maxKuPoint = -1;
        for (int i = 0; i < colInteraction.length; i++) {
            if (colInteraction[i][1] >= 0.0 && colInteraction[i][1] <= colElasticBuckCap) {
                compressionPoints++;
                if (i < minKuPoint) {
                    minKuPoint = i;
                }
                if (i > maxKuPoint) {
                    maxKuPoint = i;
                }
            }
        }
        for (int i = minKuPoint; i <= maxKuPoint; i++) {
            double slendernessLimit = this.columnSlender(colInteraction[i][1], -1.0, 1.0);
            if (colSlenderness <= slendernessLimit) {
                colDeltaB = 1.0;
            } else {
                colDeltaB = columnDeltaB(mColKm, colInteraction[i][1], colElasticBuckCap);
            }
            colMagnifiedMinMoment = colInteraction[i][1] * 0.05 * xDim / 1000.0 * colDeltaB;
            if (colMagnifiedMinMoment <= colInteraction[i][2]) {
                colAxialCapacity = colInteraction[i][1];
            }
        }
        return colAxialCapacity;
    }

    public void printColCapacity() {
        double roundedNc = Math.round(this.columnCapacitySolver() * 10) / 10.0;
        System.out.println("Column Capacity = " + roundedNc + "kN");
    }

    public String colCapacityToString() {
        double roundedNc = Math.round(this.columnCapacitySolver() * 10) / 10.0;
        String colCapacityString = Double.toString(roundedNc);
        colCapacityString = colCapacityString + " kN";
        return colCapacityString;
    }

    public int columnPresetBarNos(double dim){
        int barNumber = (int) Math.max(2,((dim-100)/150)+1);
        return barNumber;
    }

    public String getId(){
        String stringId = "";
        stringId += "xDim" + xDim + "yDim" + yDim + "bx" + bx + "by" + by + "bd" + bd + "fs" + fs + "fc" + fc +
                "cover" + cover + "ec" + eC + "es" + eS + "Es" + ES + "betaD" + betaD + "Le" + effectiveLength +
                "Lu" + mColumnLength + "sectType" + sectionType + "braceType" + bracingType + "tieSize" + mTieSize +
                "tieSpacing" + mTieSpacing;
        return stringId;
    }

    public double getxDim() {
        return xDim;
    }

    public double getyDim() {
        return yDim;
    }

    public int getFc() {
        return fc;
    }

    public int getBd() {
        return bd;
    }

    public int getBx() {
        return bx;
    }

    public int getBy() {
        return by;
    }

    public double getEffectiveLength() {
        return effectiveLength;
    }

    public int getPresetBarSize(){
        return 12; // This needs to be updated
    }

    public int getPresetBarsX(){
        return 2; // This needs to be updated
    }

    public int getPresetBarsY(){
        return 2; // This needs to be updated
    }
}