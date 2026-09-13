package ca.soccer1992.lavaproxy.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

public class EncryptionEncoder extends MessageToByteEncoder<ByteBuf>    {
    private final Cipher cipher;
    public static Cipher createCipher(int mode, byte[] sharedSecret) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        SecretKeySpec key = new SecretKeySpec(sharedSecret, "AES");
        // Vanilla uses the shared secret itself as the IV too
        cipher.init(mode, key, new IvParameterSpec(sharedSecret));
        return cipher;
    }

    public EncryptionEncoder(byte[] sharedSecret) throws GeneralSecurityException {
        this.cipher = createCipher(Cipher.ENCRYPT_MODE, sharedSecret);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        byte[] plain = new byte[msg.readableBytes()];
        msg.readBytes(plain);

        byte[] encrypted = cipher.update(plain);
        out.writeBytes(encrypted);
    }
}
