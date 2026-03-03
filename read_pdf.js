const fs = require('fs');
const { PDFParse } = require('pdf-parse');

const pdfPath = 'PayMedia_CRM_BRD_v2fads.pdf';

async function extractPDF() {
    try {
        const dataBuffer = fs.readFileSync(pdfPath);
        const data = await PDFParse(dataBuffer);
        
        console.log('=== PDF CONTENT START ===');
        console.log(data.text);
        console.log('=== PDF CONTENT END ===');
        console.log('\n=== PDF METADATA ===');
        console.log('Total Pages:', data.numpages);
        console.log('Info:', JSON.stringify(data.info, null, 2));
    } catch (error) {
        console.error('Error:', error);
        process.exit(1);
    }
}

extractPDF();
