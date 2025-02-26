'use client';
import React, { useState } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Modal from '@mui/material/Modal';
import GoogleSignIn from './GoogleSignIn';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: '#888',
  boxShadow: 6,
  p: 4,
  borderRadius: '4px',
};

export default function SignInModal() {
  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  return (
    <div>
        <div id='button-wrapper' className='border border-white rounded-md'>
      <Button className='text-white bg-zinc-600' onClick={handleOpen}>Sign In</Button>
      </div>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <h2 className='rounded-lg text-4xl font-bold text-center mb-4'>
            Sign in:
          </h2>
          <GoogleSignIn />
        </Box>
      </Modal>
    </div>
  );
}