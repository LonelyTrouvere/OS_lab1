import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

interface Foo{
    Double call(Double eps);
}

public class Server {

    private static final int ATEMPTS = 4;
    private static String exept = "";
    public static void main(String[] args) throws IOException {
        System.out.println("Waiting for connection...");
        ServerSocket ss = new ServerSocket(8080);
        Socket soc = ss.accept();
        System.out.println("Connected!");

        BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        String str = in.readLine();
        Double eps = Double.valueOf(str);

        long startTime = System.nanoTime();

        CompletableFuture<Double> cf1 = CompletableFuture.supplyAsync(() -> {
            try {
                return crashHandler((x) -> f(x), eps);
            } catch (Exception e) {
                exept = e.getMessage();
                return 0.0;
            }
        });

        CompletableFuture<Double> cf2 = CompletableFuture.supplyAsync(() -> {
            try {
                return crashHandler((x) -> g(x), eps);
            } catch (Exception e) {
                exept = e.getMessage();
                return 0.0;
            }
        });

        CompletableFuture<Double>[] funcs = new CompletableFuture[] { cf1, cf2 };
        CompletableFuture.allOf(funcs).join();

        Double[] ans = Arrays.stream(funcs).map(CompletableFuture::join).toArray(Double[]::new);

        PrintWriter out = new PrintWriter(soc.getOutputStream(), true);
        if (exept == ""){
            out.println("Returned: " + (ans[0]+ans[1]));
        } else {
            out.println(exept);
        }

        long endTime = System.nanoTime();

        System.out.println(endTime - startTime);

        soc.close();
        ss.close();
    }

    private static Double f(Double eps){
        Double x = 0.0;
        Double n = 0.0;

        long start = System.currentTimeMillis();
        long end = start + 2 * 1000;

        while(System.currentTimeMillis() < end){
            Double s;
            s = (Math.pow(-1, n))/(2*n+1);

            if (Math.abs(s) < eps){
                return 4*x;
            }

            x += s;
            n++;
        }

        return 0.0;
    }

    private static Double g(Double eps){
        Double x = 0.0;
        Double n = 0.0;

        long start = System.currentTimeMillis();
        long end = start + 2 * 1000;

        while(System.currentTimeMillis() < end){
            Double s;
            s = 1/((2*n+1)*(2*n+2));

            if (Math.abs(s) < eps){
                return x;
            }

            x += s;
            n++;
        }

        return x;
    }

    private static Double crashHandler(Foo func, Double eps) throws Exception{
        if (eps >= 1 || eps <= 0)
            throw new Exception("Not an epsilon");

        if (eps <= Math.pow(10, -10))
            throw new Exception("Epsilon to low");

        Double res = 0.0;
        int at = 0;

        while(at < ATEMPTS){
            res = func.call(eps);

            if (res != 0.0)
                break;
            else
                System.out.println("Non breaking error");

            at++;
        }

        if (res == 0.0){
            throw new Exception("Time limit excided");
        } else {
            return res;
        }
    }
}
