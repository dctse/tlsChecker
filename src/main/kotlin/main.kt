import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import javax.net.ssl.*

class TLSSocketFactory : SSLSocketFactory() {
    private val internalSSLSocketFactory: SSLSocketFactory
    override fun getDefaultCipherSuites(): Array<String> {
        return internalSSLSocketFactory.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return internalSSLSocketFactory.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket())
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(s, host, port, autoClose))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int): Socket {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port, localHost, localPort))
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(address, port, localAddress, localPort))
    }

/*    private fun enableTLSOnSocket(socket: Socket): Socket {
        if (socket != null && socket is SSLSocket) {
            socket.enabledProtocols = arrayOf("TLSv1.1", "TLSv1.2")
        }
        return socket
    }*/

    private fun enableTLSOnSocket(socket: Socket): Socket {
        if (socket is SSLSocket
                && isTLSServerEnabled((socket))) { // skip the fix if server doesn't provide there TLS version
            socket.enabledProtocols = arrayOf("TLSv1.2")
        }
        return socket
    }

    private fun isTLSServerEnabled(sslSocket: SSLSocket): Boolean {
        for (protocol in sslSocket.supportedProtocols) {
            //println(protocol)
            if (protocol == "TLSv1.2") {
                return true
            }
        }
        return false
    }

    init {
        val context: SSLContext = SSLContext.getInstance("TLS")
        context.init(null, null, null)
        internalSSLSocketFactory = context.getSocketFactory()
    }
}

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Please provide 1st argument = hostname|IP_ADDR && 2nd argument = PORT")
        return
    }

    println("testing ${args[0]}:${args[1]}")

    val hostName : String = args[0]
    val port: Int = args[1].toInt()
    try {
        val tlsSocketFactory = TLSSocketFactory()
        val socket = tlsSocketFactory.createSocket(hostName, port)

        socket.use {
            println("{ \"isTLSConnected\": ${it.isConnected}, \"remoteSocketAddress\": ${it.remoteSocketAddress} }")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
