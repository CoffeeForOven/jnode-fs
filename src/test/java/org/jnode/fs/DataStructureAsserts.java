package org.jnode.fs;

import org.jnode.fs.xfs.XfsEntry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DataStructureAsserts
{
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static void assertStructure(FileSystem<?> fileSystem, String expected) throws IOException
    {
        StringBuilder actual = new StringBuilder(expected.length());

        actual.append(String.format("type: %s vol:%s total:%d free:%d\n",
                fileSystem.getType().getName(), fileSystem.getVolumeName(),
                fileSystem.getTotalSpace(), fileSystem.getFreeSpace()));

        FSEntry entry = fileSystem.getRootEntry();
        buildStructure(entry, actual, "  ");

        assertThat(actual.toString(), is(expected));

    }

    /**
     * Builds up the structure for the given file system entry.
     *
     * @param entry  the entry to process.
     * @param actual the string to append to.
     * @param indent the indent level.
     * @throws IOException if an error occurs.
     */
    private static void buildStructure(FSEntry entry, StringBuilder actual, String indent) throws IOException
    {
        actual.append(indent);
        actual.append(entry.getName());
        actual.append("; ");

        if (entry.isFile())
        {
            FSFile file = entry.getFile();
            actual.append(file.getLength());
            actual.append("; ");
            actual.append(getMD5Digest(file));
            actual.append("\n");

            if (file instanceof FSFileStreams)
            {
                Map<String, FSFile> streams = ((FSFileStreams) file).getStreams();

                for (Map.Entry<String, FSFile> streamEntry : streams.entrySet())
                {
                    actual.append(indent);
                    actual.append(entry.getName());
                    actual.append(":");
                    actual.append(streamEntry.getKey());
                    actual.append("; ");
                    actual.append(streamEntry.getValue().getLength());
                    actual.append("; ");
                    actual.append(getMD5Digest(streamEntry.getValue()));
                    actual.append("\n");
                }
            }

        }
        else
        {
            actual.append("\n");

            FSDirectory directory = entry.getDirectory();
            Iterator<? extends FSEntry> iterator = directory.iterator();

            while (iterator.hasNext())
            {
                FSEntry child = iterator.next();

                if (".".equals(child.getName()) || "..".equals(child.getName()))
                {
                    continue;
                }

                buildStructure(child, actual, indent + "  ");
            }
        }
    }

    public static String getMD5Digest(FSFile file) throws IOException
    {
        MessageDigest md5;

        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("Couldn't find MD5");
        }

        byte[] buffer = new byte[0x1000];
        long position = 0;
        long length = file.getLength();
        while (position < length)
        {
            int chunkLength = (int) Math.min(length - position, buffer.length);
            file.read(position, ByteBuffer.wrap(buffer, 0, chunkLength));
            md5.update(buffer, 0, chunkLength);
            position += chunkLength;
        }

        return toHexString(md5.digest()).toLowerCase();
    }

    private static String toHexString(byte[] digest)
    {
        StringBuilder builder = new StringBuilder(digest.length * 2);

        for (int offset = 0; offset < digest.length; offset++)
        {
            builder.append(HEX_DIGITS[(digest[offset] >> 4) & 0x0F]);
            builder.append(HEX_DIGITS[digest[offset] & 0x0F]);
        }

        return builder.toString();
    }

    /**
     * Builds up the structure for the given file system entry to get the metadata.
     *
     * @param entry  the entry to process.
     * @param actual the string to append to.
     * @param indent the indent level.
     * @throws IOException if an error occurs.
     */
    public static void buildXfsMetaDataStructure(FSEntry entry, StringBuilder actual, String indent) throws IOException
    {
        actual.append(indent);
        actual.append(entry.getName());
        actual.append("; \n");

        if (entry.isDirectory()) {
            getXfsMetadata(entry, actual, indent);
        }
        if (entry.isFile()) {
            FSFile file = entry.getFile();
            getXfsMetadata(entry, actual, indent);
        }
        else {
            FSDirectory directory = entry.getDirectory();

            Iterator<? extends FSEntry> iterator = directory.iterator();

            while (iterator.hasNext()) {
                FSEntry child = iterator.next();

                if (".".equals(child.getName()) || "..".equals(child.getName()))
                {
                    continue;
                }

                buildXfsMetaDataStructure(child, actual, indent + "  ");
            }
        }
    }

    /**
     * Get the metadata.
     *
     * @param entry  the entry to process.
     * @param actual the string to append to.
     * @param indent the indent level.
     *
     */
    private static StringBuilder getXfsMetadata(FSEntry entry, StringBuilder actual,String indent) {
        actual.append(indent);
        actual.append(indent);
        actual.append("atime : " +  getDate(((XfsEntry) entry).getINode().getAccessTime()));
        actual.append("; ");
        actual.append("ctime : " +  getDate(((XfsEntry) entry).getINode().getCreatedTime()));
        actual.append("; ");
        actual.append("mtime : " +  getDate(((XfsEntry) entry).getINode().getModifiedTime()) +"\n" );
        actual.append(indent);
        actual.append(indent);
        actual.append("owner : " + ((XfsEntry) entry).getINode().getUid() );
        actual.append("; ");
        actual.append("group : " + ((XfsEntry) entry).getINode().getGid() );
        actual.append("; ");
        actual.append("size : " +  ((XfsEntry) entry).getINode().getSize() );
        actual.append("; ");
        String mode = Integer.toOctalString(((XfsEntry) entry).getINode().getMode());
        actual.append("mode : " +  mode.substring(mode.length()-3));
        actual.append("; \n");

        return actual;
    }

    /**
     * Convert epoch to human-readable date.
     *
     * @param date  the epoch value.
     */
    private static String getDate(long date) {
        long tempLong = (int) (date >> 32);
        return new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(tempLong * 1000));
    }
}