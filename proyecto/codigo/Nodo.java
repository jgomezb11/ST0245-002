import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.LinkedList;

/**
 * La clase Nodo se encarga de empezar a crear el arbol dividiendo cada nodo hasta cierta cantidad.
 *
 * @author Juan Pablo Rincon - Julian Gomez Benitez
 */
public class Nodo{
    private String[][] matriz;
    private Nodo izq;
    private Nodo der;
    private int laColumna;
    private String elValor;

    ImpurezaDeGini idg = new ImpurezaDeGini();
    Datos datos = new Datos();
    int longitudInicial = datos.longitud;
    public static LinkedList<Pair<Integer, String>> struct2 = new  LinkedList<Pair<Integer, String>>();
    int[] d = new int[]{0, 1, 2, 3, 4, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 32, 33, 34, 37, 38, 39, 40, 41, 43, 45, 46, 47, 48, 49, 50, 52, 53, 56, 57, 58, 59, 61, 62, 63, 64, 74, 75, 76};
    List<Integer> you = Arrays.stream(d).boxed().collect(Collectors.toList());
    TreeSet<Integer> valoresNoPermitidos = new TreeSet<Integer>();

    /**
     * Constructor encargado de generar cada nodo del árbol, donde se genera priemro el lado izquierdo y después el lado derecho
     * aquí se hara el uso de la impureza de gini para poder dividir la matriz, se dividira el nodo hasta que sea menor o igual al
     * 33% de la longitud inicial, después se procede a saber cuantas persoans predijo.
     * @param m la matriz de estudiantes.
     * @use calcularSiHayExitoONOEnUnaMatriz(m).
     * @use encontrarEnQuePosEstaLaMejorVariableYQueValorDeboCompararEnLaCondicion(m).
     * @use impurezaPonderada(m, laColumna, elValor)
     */
    public Nodo (String[][] m){
        valoresNoPermitidos.addAll(you);
        if (m.length <= (longitudInicial*33)/100){
            matriz = m;
            calcularSiHayExitoONOEnUnaMatriz(m);
        }
        else{
            matriz = m;
            Pair<Integer,String> dato = encontrarEnQuePosEstaLaMejorVariableYQueValorDeboCompararEnLaCondicion(m);
            struct2.add(dato);
            laColumna = dato.first;
            elValor = dato.second;
            idg.impurezaPonderada(m, laColumna, elValor);
            String[][] mI = idg.matrizValoresDiferentes;
            String[][] mD = idg.matrizValoresIguales;
            Nodo izq = new Nodo(mI);
            Nodo der = new Nodo(mD);
        }
    }

    /**
     * Método encargado de revisar las metricas de evaluación para una hoja.
     * @param m matriz menor o igual del 33% de la longitud inicial osea una hoja del árbol creado.
     * @use datosRelevantes().
     */
    public void calcularSiHayExitoONOEnUnaMatriz(String[][] m){
        float verdaderosPositivos = 0;
        float verdaderosTotales = Datos.datosRelevantes();
        for(int i = 0; i < m.length; i++){
            if(m[i][m[0].length-1].equals("0")){
                verdaderosPositivos++;
            }
        }
        System.out.println("La precisión es: " + (verdaderosPositivos/m.length));
        System.out.println("Los recuperados son: " + (verdaderosPositivos/verdaderosTotales));
        //System.out.println("La exactitud es: " + ((verdaderosPositivos + (verdaderosPositivos-m.length))/longitudInicial));
    }

    /**
     * Método encargado de sacar las posibles respuestas dadas por los estudiantes, para una pegunta, sin repetirla.
     * @param m matriz de los estudiantes.
     * @param posVariable posición de la pregunta que responden los estudiantes.
     * @return las posibles respuestas dadas para esa pregunta(sin repetirlas).
     */
    public TreeSet<String> sacarLosValoresDiferentesSinRepetirDeUnaVariable(String[][] m, int posVariable){
        TreeSet<String> respuesta = new TreeSet<String>();
        for (int fila = 0; fila < m.length; fila++)
            respuesta.add(m[fila][posVariable]);
        return respuesta;
    }

    /**
     * Método encargado de revisar cada posible respuesta y mirar cual de ellas produce una impureza de gini ponderada más baja.
     * @param m matriz de los estudiantes.
     * @return una pareja, el primer objeto del par es la pregunta que produce menos impureza(posición matriz) y el segundo es la respuesta que produce menos impureza(valor).
     * @use sacarLosValoresDiferentesSinRepetirDeUnaVariable(m, columna).
     */
    public Pair<Integer,String> encontrarEnQuePosEstaLaMejorVariableYQueValorDeboCompararEnLaCondicion(String[][] m){
        TreeSet<String> valores = null;
        String elMejorValorDentreTodoElMundo = "";
        float laImpurezaMenorDentreTodoElmundo = 1;
        int laPosDeLaVariableDondeEstaElMejorValor = -1;
        for (int columna = 0; columna < m[0].length - 1; columna++) {
            if(valoresNoPermitidos.contains(columna)){
                continue;
            }
            valores = sacarLosValoresDiferentesSinRepetirDeUnaVariable(m, columna);
            int count = 0;
            for (String unValor : valores) {
                float impurezaPonderadaDeEstaColumnaConEsteValor;
                if((!unValor.isEmpty())&&(unValor.matches("^[0-9]*[.][0-9]*$")) && count == 0){
                    impurezaPonderadaDeEstaColumnaConEsteValor = idg.impurezaPonderada(m, columna, 80.0f);
                    count++;
                }else{
                    impurezaPonderadaDeEstaColumnaConEsteValor = idg.impurezaPonderada(m, columna, unValor);
                }
                if (impurezaPonderadaDeEstaColumnaConEsteValor < laImpurezaMenorDentreTodoElmundo){
                    laImpurezaMenorDentreTodoElmundo = impurezaPonderadaDeEstaColumnaConEsteValor;
                    elMejorValorDentreTodoElMundo = unValor;
                    laPosDeLaVariableDondeEstaElMejorValor = columna;
                }
            }
        }
        Pair<Integer,String> respuesta = new Pair<Integer,String>(laPosDeLaVariableDondeEstaElMejorValor, elMejorValorDentreTodoElMundo);
        return respuesta;
    }
}
