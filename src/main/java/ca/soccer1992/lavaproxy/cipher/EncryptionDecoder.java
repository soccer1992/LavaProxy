package ca.soccer1992.lavaproxy.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.List;

public class EncryptionDecoder extends ByteToMessageDecoder {
    private final Cipher cipher;
    public static Cipher createCipher(int mode, byte[] sharedSecret) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        SecretKeySpec key = new SecretKeySpec(sharedSecret, "AES");
        // Vanilla uses the shared secret itself as the IV too
        cipher.init(mode, key, new IvParameterSpec(sharedSecret));
        return cipher;
    }

    public EncryptionDecoder(byte[] sharedSecret) throws GeneralSecurityException {
        this.cipher = createCipher(Cipher.DECRYPT_MODE, sharedSecret);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readable = in.readableBytes();
        if (readable == 0) return;

        byte[] encrypted = new byte[readable];
        in.readBytes(encrypted);

        byte[] decrypted = cipher.update(encrypted);
        out.add(ctx.alloc().buffer(decrypted.length).writeBytes(decrypted));
    }
}
